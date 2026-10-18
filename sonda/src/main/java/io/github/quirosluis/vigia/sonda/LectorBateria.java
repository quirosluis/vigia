package io.github.quirosluis.vigia.sonda;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class LectorBateria {

    // Si el programa externo se cuelga, no esperamos para siempre.
    private static final long TIEMPO_MAXIMO_SEGUNDOS = 10;

    private LectorBateria() {
    }

    public static EstadoBateria leer()
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder("termux-battery-status");
        pb.redirectErrorStream(true);          // errores y salida normal en un solo texto

        Process proceso = pb.start();          // IOException si el programa no existe

        boolean terminado = proceso.waitFor(TIEMPO_MAXIMO_SEGUNDOS, TimeUnit.SECONDS);
        if (!terminado) {
            proceso.destroyForcibly();         // lo matamos para no dejar procesos colgados
            throw new IllegalStateException(
                    "termux-battery-status no respondió en " + TIEMPO_MAXIMO_SEGUNDOS + " s");
        }

        String salida = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int codigo = proceso.exitValue();      // ya ha terminado: el código está disponible


        if(codigo!=0){
            throw new IllegalStateException("Comando termux-battery-status finalizó con código " + codigo+": '"+ salida.trim()+"'");
        }

        return BateriaParser.parsear(salida);
    }
}




