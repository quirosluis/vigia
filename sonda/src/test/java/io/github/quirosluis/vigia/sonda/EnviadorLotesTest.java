package io.github.quirosluis.vigia.sonda;

import com.sun.net.httpserver.HttpServer;
import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EnviadorLotesTest {
    private HttpServer servidor;

    @AfterEach
    void para() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    private URI servidorQueResponde(int codigo, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);   // puerto libre
        servidor.createContext("/api/heartbeats", exchange -> {
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(codigo, bytes.length == 0 ? -1 : bytes.length);
            if (bytes.length > 0) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        });
        servidor.start();
        return URI.create("http://127.0.0.1:" + servidor.getAddress().getPort() + "/api/heartbeats");
    }

    private static List<Medicion> trozo() {
        return List.of(new Medicion(Instant.parse("2026-11-02T10:00:00Z"), "router", true, 4, 80, true));
    }

    private Enviador enviadorContra(URI destino) {
        return new EnviadorLotes(destino, "torre", "sonda1");      // el tipo de la variable es la interfaz
    }


    @Test
    void un202SeConsideraEnviado() throws IOException {
        assertThat(enviadorContra(servidorQueResponde(202, "")).enviar(trozo()))
                .isEqualTo(Enviador.Resultado.ENVIADO);
    }

    @Test
    void un400SeConsideraRechazado() throws IOException {
        assertThat(enviadorContra(servidorQueResponde(400, "{\"errores\":[]}")).enviar(trozo()))
                .isEqualTo(Enviador.Resultado.RECHAZADO);
    }

    @Test
    void un500SeReintenta() throws IOException {
        assertThat(enviadorContra(servidorQueResponde(500, "")).enviar(trozo()))
                .isEqualTo(Enviador.Resultado.REINTENTAR);
    }

    @Test
    void sinServidorSeReintenta() throws IOException {
        URI destino = servidorQueResponde(202, "");
        servidor.stop(0);                                          // el puerto queda cerrado
        assertThat(enviadorContra(destino).enviar(trozo()))
                .isEqualTo(Enviador.Resultado.REINTENTAR);
    }
}
