package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ReglaSinProblemas implements Regla {

    private static final int VENTANA_CICLOS = 4;
    private static final int MINIMO_CICLOS = 2;
    private static final Set<String> OBJETIVOS = Set.of("router", "internet", "dns");

    @Override
    public Optional<Diagnostico> evaluar(List<Medicion> mediciones) {

        Map<Instant, List<Medicion>> porMarca = mediciones.stream()
                .collect(Collectors.groupingBy(Medicion::timestampUtc, TreeMap::new, Collectors.toList()));

        List<List<Medicion>> completas = porMarca.values().stream()
                .filter(ReglaSinProblemas::estaCompleta)
                .toList();

        if(completas.size() < MINIMO_CICLOS) {
            return Optional.empty();
        }

        List<List<Medicion>> ventana = completas.subList(
                Math.max(0, completas.size() - VENTANA_CICLOS), completas.size());

        boolean todoCorrecto = ventana.stream().flatMap(List::stream).allMatch(Medicion::ok);
        if(!todoCorrecto) {
            return Optional.empty();
        }

        return Optional.of(new Diagnostico(Causa.SIN_PROBLEMAS, ventana.size()+" ciclos completos con router, internet y dns en ok"));
    }

    private static boolean estaCompleta(List<Medicion> ventana) {
        Set<String> objetivos = ventana.stream().map(Medicion::objetivo).collect((Collectors.toSet()));
        return objetivos.containsAll(OBJETIVOS);
    }

}
