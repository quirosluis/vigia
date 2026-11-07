package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.util.List;
import java.util.Optional;

public interface Regla {
    Optional<Diagnostico> evaluar(List<Medicion> ventana);
}
