package io.github.quirosluis.vigia.backend.diagnostico;

public record Diagnostico(Causa causa, String evidencia) {

    public Diagnostico {

        if(causa == null) {
            throw new IllegalArgumentException("causa es obligatoria");
        }

        if(evidencia == null || evidencia.isBlank()) {
            throw new IllegalArgumentException("evidencia es obligatoria");
        }

    }
}
