package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.quirosluis.vigia.backend.diagnostico.CiclosDePrueba.*;
import static org.assertj.core.api.Assertions.assertThat;


class ReglaSinProblemasTest {

    private final ReglaSinProblemas regla = new ReglaSinProblemas();

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
    void unFalloEnLaVentanaInternetImpideDecirlo() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, false, true),      // un fallo
                ciclo("2026-11-08T10:00:30Z", true, true, true));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void unFalloEnLaVentanaDNSImpideDecirlo() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, true, true),      // un fallo
                ciclo("2026-11-08T10:00:30Z", true, true, false));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void unFalloEnLaVentanaRouterImpideDecirlo() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", false, true, true),
                ciclo("2026-11-08T10:00:15Z", true, true, true),      // un fallo
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
                List.of(m("2026-11-08T10:00:15Z", "router", true)));   // faltan internet y dns

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
