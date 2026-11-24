package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


final class VentanaCiclos {

    private static final int VENTANA_CICLOS = 4;
    private static final int MINIMO_CICLOS = 2;
    private static final Set<String> OBJETIVOS = Set.of("router", "internet", "dns");

    private VentanaCiclos() { }

    /** Todas los ciclos completos, ordenados por marca. Puede estar vacía. */
    static List<List<Medicion>> completas(List<Medicion> mediciones) {
        Map<Instant, List<Medicion>> porMarca = mediciones.stream()
                .collect(Collectors.groupingBy(Medicion::timestampUtc, TreeMap::new, Collectors.toList()));

        return porMarca.values().stream()
                .filter(VentanaCiclos::estaCompleta)
                .toList();
    }

    static Optional<List<List<Medicion>>> ultimos(List<Medicion> mediciones) {
        List<List<Medicion>> completas = completas(mediciones);
        if (completas.size() < MINIMO_CICLOS) {
            return Optional.empty();
        }
        return Optional.of(completas.subList(
                Math.max(0, completas.size() - VENTANA_CICLOS), completas.size()));
    }

    private static boolean estaCompleta(List<Medicion> ciclo) {
        Set<String> objetivos = ciclo.stream().map(Medicion::objetivo).collect((Collectors.toSet()));
        return objetivos.containsAll(OBJETIVOS);
    }
}


