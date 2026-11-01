package io.github.quirosluis.vigia.sonda;

public record MedicionEnvio(String timestampUtc, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando) {
}
