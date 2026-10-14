package io.github.quirosluis.vigia;

import java.time.Instant;

public record Medicion(Instant timestampUtc, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando ) {}
