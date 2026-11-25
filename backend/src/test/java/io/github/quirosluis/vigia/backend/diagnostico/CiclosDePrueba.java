package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

final class CiclosDePrueba {

    private CiclosDePrueba() {}

    static Medicion m(String marca, String objetivo, boolean ok) {
        return new Medicion(Instant.parse(marca), objetivo, ok, ok ? 5 : null, 80, true);
    }

    static List<Medicion> ciclo(String marca, boolean router, boolean internet, boolean dns) {
        return List.of(m(marca, "router", router), m(marca, "internet", internet), m(marca, "dns", dns));
    }

    @SafeVarargs
    static List<Medicion> unir(List<Medicion>... ciclos) {
        return Stream.of(ciclos).flatMap(List::stream).toList();
    }

}
