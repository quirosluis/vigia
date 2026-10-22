package io.github.quirosluis.vigia;

import java.time.Instant;

public record Medicion(Instant timestampUtc, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando ) {

    private static final int MAX_BATERIA = 100;
    private static final int MIN_BATERIA = 0;

    public Medicion {
        if (timestampUtc == null){
            throw new IllegalArgumentException("timestampUtc debe estar informado");
        }
        if (objetivo == null || objetivo.isBlank()){
            throw new IllegalArgumentException("objetivo debe estar informado: "+objetivo);
        }
        if (ok) {
            if(latenciaMs == null) {
                throw new IllegalArgumentException("latenciaMs debe estar informado");
            }
            if (latenciaMs < 0){
                throw new IllegalArgumentException("latenciaMs no puede ser negativa: "+latenciaMs);
            }
        }

        if(!ok) {
            latenciaMs = null;
        }
        if ( bateria > MAX_BATERIA|| bateria < MIN_BATERIA) {
            throw new IllegalArgumentException("El campo bateria está fuera de rango ("+MIN_BATERIA+"-"+MAX_BATERIA+"): "+bateria);
        }
    }

}
