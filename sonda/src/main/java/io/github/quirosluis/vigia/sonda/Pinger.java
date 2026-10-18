package io.github.quirosluis.vigia.sonda;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class Pinger {

    // Si el programa externo se cuelga, no esperamos para siempre.
    private static final long TIEMPO_MAXIMO_SEGUNDOS = 10;

    private Pinger() {}

    public static PingResult medir(String destino)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", "-W", "2", destino);
        pb.redirectErrorStream(true);          // errores y salida normal en un solo texto

        Process proceso = pb.start();          // IOException si el programa no existe

        boolean terminado = proceso.waitFor(TIEMPO_MAXIMO_SEGUNDOS, TimeUnit.SECONDS);
        if (!terminado) {
            proceso.destroyForcibly();         // lo matamos para no dejar procesos colgados
            throw new IllegalStateException(
                    "ping -c 1 -W 2 "+ destino +" no respondió en " + TIEMPO_MAXIMO_SEGUNDOS + " s");
        }

        String salida = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int codigo = proceso.exitValue();      // ya ha terminado: el código está disponible

        return PingParser.parsear(salida, codigo);
    }
}




