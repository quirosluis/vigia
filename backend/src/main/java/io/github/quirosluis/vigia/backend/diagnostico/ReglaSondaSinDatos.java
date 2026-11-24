package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class ReglaSondaSinDatos implements Regla {

    static final Duration EDAD_MAXIMA = Duration.ofMinutes(5);
    private final Clock reloj;

    public ReglaSondaSinDatos(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public Optional<Diagnostico> evaluar(List<Medicion> mediciones) {

        List<List<Medicion>> completas = VentanaCiclos.completas(mediciones);
        if (completas.isEmpty()) {
            return Optional.of(new Diagnostico(Causa.SONDA_SIN_DATOS,"ningún ciclo completo en los datos recibidos"));
        }
        Instant ultimo = completas.get(completas.size() - 1).get(0).timestampUtc();   // ya vienen ordenados
        Instant limite = reloj.instant().minus(EDAD_MAXIMA);
        if (ultimo.isBefore(limite)) {
            long minutos = Duration.between(ultimo, reloj.instant()).toMinutes();
            return Optional.of(new Diagnostico(Causa.SONDA_SIN_DATOS, "último ciclo completo hace " + minutos + " min"));
        }
        return Optional.empty();
    }
}
