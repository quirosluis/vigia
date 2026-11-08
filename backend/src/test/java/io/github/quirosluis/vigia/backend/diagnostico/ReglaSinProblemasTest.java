package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReglaSinProblemasTest {

    private final ReglaSinProblemas regla = new ReglaSinProblemas();

    private static Medicion m(String marca, String objetivo, boolean ok) {
        return new Medicion(Instant.parse(marca), objetivo, ok, ok ? 5 : null, 80, true);
    }

    private static List<Medicion> ciclo(String marca, boolean router, boolean internet, boolean dns) {
        return List.of(m(marca, "router", router), m(marca, "internet", internet), m(marca, "dns", dns));
    }

    @SafeVarargs
    private static List<Medicion> unir(List<Medicion>... ciclos) {
        return Stream.of(ciclos).flatMap(List::stream).toList();
    }

    @Test
    void cuatroCiclosCorrectosSonSinProblemas() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, true, true),
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                ciclo("2026-11-08T10:00:45Z", true, true, true));

        assertThat(regla.evaluar(ciclos)).hasValueSatisfying(
                d -> assertThat(d.causa()).isEqualTo(Causa.SIN_PROBLEMAS));
    }

    @Test
    void unFalloEnLaVentanaImpideDecirlo() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, false, true),      // un fallo
                ciclo("2026-11-08T10:00:30Z", true, true, true));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void conUnSoloCicloNoOpina() {
        assertThat(regla.evaluar(ciclo("2026-11-08T10:00:00Z", true, true, true))).isEmpty();
    }

    @Test
    void conUnCicloIncompletoNoCuenta() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                List.of(m("2026-11-08T10:00:15Z", "router", true)));   // faltan firma y pago

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void losFallosAntiguosFueraDeLaVentanaNoCuentan() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", false, false, false),    // fuera: solo se miran las últimas 4
                ciclo("2026-11-08T10:00:15Z", true, true, true),
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                ciclo("2026-11-08T10:00:45Z", true, true, true),
                ciclo("2026-11-08T10:01:00Z", true, true, true));

        assertThat(regla.evaluar(ciclos)).isPresent();
    }

    @Test
    void unaListaVaciaNoOpina() {
        assertThat(regla.evaluar(List.of())).isEmpty();
    }


}
