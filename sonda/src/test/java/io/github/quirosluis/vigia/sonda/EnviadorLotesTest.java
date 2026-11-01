package io.github.quirosluis.vigia.sonda;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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

    @Test
    void un202SeConsideraEnviado() throws IOException {
        assertThat(new EnviadorLotes(servidorQueResponde(202, "")).enviar("{}"))
                .isEqualTo(EnviadorLotes.Resultado.ENVIADO);
    }

    @Test
    void un400SeConsideraRechazado() throws IOException {
        assertThat(new EnviadorLotes(servidorQueResponde(400, "{\"errores\":[]}")).enviar("{}"))
                .isEqualTo(EnviadorLotes.Resultado.RECHAZADO);
    }

    @Test
    void un500SeReintenta() throws IOException {
        assertThat(new EnviadorLotes(servidorQueResponde(500, "")).enviar("{}"))
                .isEqualTo(EnviadorLotes.Resultado.REINTENTAR);
    }

    @Test
    void sinServidorSeReintenta() throws IOException {
        URI destino = servidorQueResponde(202, "");
        servidor.stop(0);                                  // el puerto queda cerrado
        assertThat(new EnviadorLotes(destino).enviar("{}"))
                .isEqualTo(EnviadorLotes.Resultado.REINTENTAR);
    }
}
