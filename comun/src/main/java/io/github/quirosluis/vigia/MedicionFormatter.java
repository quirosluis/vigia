package io.github.quirosluis.vigia;


public final class MedicionFormatter {

    private MedicionFormatter() {
    }

    public static String formatear(Medicion m) {

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
