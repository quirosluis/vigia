package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.util.List;
import static io.github.quirosluis.vigia.backend.diagnostico.CiclosDePrueba.*;


import static org.assertj.core.api.Assertions.assertThat;

class VentanaCicloTest {

    @Test
    void cicloIncompletoIgnorado() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, true, true),
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                List.of(m("2026-11-08T10:45:15Z", "router", true)));

        assertThat(VentanaCiclos.ultimos(ciclos))
                .hasValueSatisfying(v -> {
                    assertThat(v).hasSize(3);
                    assertThat(v).extracting(c -> c.get(0).timestampUtc())
                            .containsExactly(
                                    Instant.parse("2026-11-08T10:00:00Z"),
                                    Instant.parse("2026-11-08T10:00:15Z"),
                                    Instant.parse("2026-11-08T10:00:30Z"));
                });
    }

    @Test
    void conMasDeCuatroCiclosRetornaCuatroPorOrdenCronologico() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true),
                ciclo("2026-11-08T10:00:15Z", true, false, true),      // un fallo
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                ciclo("2026-11-08T10:00:45Z", true, true, true),
                ciclo("2026-11-08T10:01:00Z", true, true, true));
        assertThat(VentanaCiclos.ultimos(ciclos))
                .hasValueSatisfying(v -> {
                    assertThat(v).hasSize(4);
                    assertThat(v).extracting(c -> c.get(0).timestampUtc())
                            .containsExactly(
                                    Instant.parse("2026-11-08T10:00:15Z"),
                                    Instant.parse("2026-11-08T10:00:30Z"),
                                    Instant.parse("2026-11-08T10:00:45Z"),
                                    Instant.parse("2026-11-08T10:01:00Z"));
                });
    }

    @Test
    void menosMinimoCiclosRetornaVacio() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:00Z", true, true, true));
        assertThat(VentanaCiclos.ultimos(ciclos)).isEmpty();
    }

    @Test
    void conCiclosDesordenadosRetornaCuatroPorOrdenCronologico() {
        List<Medicion> ciclos = unir(
                ciclo("2026-11-08T10:00:15Z", true, false, true),      // un fallo
                ciclo("2026-11-08T10:00:45Z", true, true, true),
                ciclo("2026-11-08T10:00:30Z", true, true, true),
                ciclo("2026-11-08T10:01:00Z", true, true, true));
        assertThat(VentanaCiclos.ultimos(ciclos))
                .hasValueSatisfying(v -> {
                    assertThat(v).hasSize(4);
                    assertThat(v).extracting(c -> c.get(0).timestampUtc())
                            .containsExactly(
                                    Instant.parse("2026-11-08T10:00:15Z"),
                                    Instant.parse("2026-11-08T10:00:30Z"),
                                    Instant.parse("2026-11-08T10:00:45Z"),
                                    Instant.parse("2026-11-08T10:01:00Z"));
                });
    }

    @Test
    void listaVaciaDevuelveVacio() {
        List<Medicion> ciclos = List.of();
        assertThat(VentanaCiclos.ultimos(ciclos)).isEmpty();
    }
}
