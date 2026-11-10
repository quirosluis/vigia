package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


final class VentanaCiclos {

    private static final int VENTANA_CICLOS = 4;
    private static final int MINIMO_CICLOS = 2;
    private static final Set<String> OBJETIVOS = Set.of("router", "internet", "dns");

    static Optional<List<List<Medicion>>> ultimos(List<Medicion> mediciones) {

        Map<Instant, List<Medicion>> porMarca = mediciones.stream()
                .collect(Collectors.groupingBy(Medicion::timestampUtc, TreeMap::new, Collectors.toList()));

        List<List<Medicion>> completas = porMarca.values().stream()
                .filter(VentanaCiclos::estaCompleta)
                .toList();

        if (completas.size() < MINIMO_CICLOS) {
            return Optional.empty();
        }

        List<List<Medicion>> ventana = completas.subList(
                Math.max(0, completas.size() - VENTANA_CICLOS), completas.size());

        return Optional.of(ventana);
    }

    private static boolean estaCompleta(List<Medicion> ventana) {
        Set<String> objetivos = ventana.stream().map(Medicion::objetivo).collect((Collectors.toSet()));
        return objetivos.containsAll(OBJETIVOS);
    }
}


