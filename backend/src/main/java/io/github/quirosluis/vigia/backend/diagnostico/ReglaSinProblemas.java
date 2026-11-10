package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.util.List;
import java.util.Optional;

public final class ReglaSinProblemas implements Regla {

    @Override
    public Optional<Diagnostico> evaluar(List<Medicion> mediciones) {

        Optional<List<List<Medicion>>> ventana = VentanaCiclos.ultimos(mediciones);
        if (ventana.isEmpty()) {
            return Optional.empty();
        }
        List<List<Medicion>> ciclos = ventana.get();

        boolean todoCorrecto = ciclos.stream().flatMap(List::stream).allMatch(Medicion::ok);
        if (!todoCorrecto) {
            return Optional.empty();
        }

        return Optional.of(new Diagnostico(Causa.SIN_PROBLEMAS, ciclos.size() + " ciclos completos con router, internet y dns en ok"));
    }
}
