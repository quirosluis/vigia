package io.github.quirosluis.vigia;


public final class MedicionFormatter {

    private MedicionFormatter() {
    }

    public static String formatear(Medicion m) {

        if (m.ok() && m.latenciaMs() == null) {
            throw new IllegalArgumentException(
                    "Una medición con ok=1 necesita latenciaMs: " + m);
        }

        return String.join(",",
                m.timestampUtc().toString(),
                m.objetivo(),
                m.ok() ? "1" : "0",
                m.ok() ? m.latenciaMs().toString() : "",
                String.valueOf(m.bateria()),
                m.cargando() ? "1" : "0"
        );
    }
}
