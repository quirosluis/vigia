package io.github.quirosluis.vigia.sonda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PingParser {

    private static final Pattern LATENCIA = Pattern.compile("time=(\\d+(?:\\.\\d+)?) ms");

    private PingParser() {
    }

    public static PingResult parsear(String salida, int codigoSalida) {

        Integer latenciaMs = null;
        boolean ok = false;
        if (salida == null) {
            throw new IllegalArgumentException("La salida del ping es null");
        }

        try {
            if (codigoSalida == 0) {
                String ms = "";
                Matcher m = LATENCIA.matcher(salida);
                if (m.find()) {
                    ms = m.group(1);   // "3.68"
                }
                long l = Math.round(Double.parseDouble(ms));
                latenciaMs = Math.toIntExact(l);
                ok = true;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudo recuperar latencia");
        }

        return (new PingResult(ok, latenciaMs));
    }
}
