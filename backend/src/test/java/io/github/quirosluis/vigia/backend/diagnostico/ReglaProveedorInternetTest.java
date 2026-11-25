package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.quirosluis.vigia.backend.diagnostico.CiclosDePrueba.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReglaProveedorInternetTest {

    private final ReglaProveedorInternet regla = new ReglaProveedorInternet();

    @Test
    void cuatroCiclosRouterOkInternetKO() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, false, true),
                ciclo("2026-11-08T10:00:15Z", true, false, true),
                ciclo("2026-11-08T10:00:30Z", true, false, true),
                ciclo("2026-11-08T10:00:45Z", true, false, true));

        assertThat(regla.evaluar(ciclos)).hasValueSatisfying(
                d -> assertThat(d.causa()).isEqualTo(Causa.PROVEEDOR_INTERNET));
    }

    @Test
    void cuatroCiclosRouterKOInternetKONoOpina() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", false, false, true),
                ciclo("2026-11-08T10:00:15Z", false, false, true),
                ciclo("2026-11-08T10:00:30Z", false, false, true),
                ciclo("2026-11-08T10:00:45Z", false, false, true));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void unCicloConInternetOkNoOpina() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, false, true),
                ciclo("2026-11-08T10:00:15Z", true, false, true),
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                ciclo("2026-11-08T10:00:45Z", true, false, true));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void menosDeDosCiclosCompletosNoOpina() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, false, true),
                List.of(m("2026-11-08T10:00:15Z", "router", true)));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void listaVaciaNoOpina() {
        List<Medicion> ciclos = List.of();
        assertThat(regla.evaluar(ciclos)).isEmpty();
    }

    @Test
    void cicloUnicoNoOpina() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, false, true));

        assertThat(regla.evaluar(ciclos)).isEmpty();
    }
}
