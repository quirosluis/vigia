package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public final class EnviadorLotes implements Enviador {

    private static final System.Logger LOG = System.getLogger(EnviadorLotes.class.getName());

    private final String sitio;
    private final String sonda;
    private final HttpClient cliente;
    private final URI destino;

    public EnviadorLotes(URI destino, String sitio, String sonda) {
        this.destino = destino;
        this.sitio = sitio;
        this.sonda = sonda;
        this.cliente = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public Resultado enviar(List<Medicion> trozo) {
        return enviarJson(LoteJson.aJson(sitio, sonda, trozo));
    }

    private Resultado enviarJson(String json) {
        HttpRequest peticion = HttpRequest.newBuilder(destino)
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            int codigo = respuesta.statusCode();
            if (codigo == 202) {
                return Resultado.ENVIADO;
            }
            if (codigo >= 400 && codigo < 500) {
                LOG.log(System.Logger.Level.WARNING, "Lote rechazado ({0}): {1}", codigo, respuesta.body());
                return Resultado.RECHAZADO;
            }
            LOG.log(System.Logger.Level.WARNING, "El servidor respondió {0}; se reintentará", codigo);
            return Resultado.REINTENTAR;
        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "Sin conexión con el servidor: {0}", e.getMessage());
            return Resultado.REINTENTAR;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Resultado.REINTENTAR;
        }
    }

}
