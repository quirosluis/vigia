package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReglaSondaSinDatosTest {

    static final Instant AHORA = Instant.parse("2026-11-12T18:00:00Z");
    private final ReglaSondaSinDatos regla = new ReglaSondaSinDatos(Clock.fixed(AHORA, ZoneOffset.UTC));


    // Último ciclo recibido hace N segundos
    private static List<Medicion> ciclos(long segundosAtras) {
        Instant marca = AHORA.minusSeconds(segundosAtras);
        return unir(
                ciclo(marca.minusSeconds(45).toString(), true, true, true),
                ciclo(marca.minusSeconds(30).toString(), true, true, true),
                ciclo(marca.minusSeconds(15).toString(), true, true, true),
                ciclo(marca.toString(), true, true, true));
    }

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
    void conDatosRecientesNoOpina() {
        assertThat(regla.evaluar(ciclos(15))).isEmpty();
    }

    @Test
    void unCicloDeHaceDiezMinutosAvisa() {
        assertThat(regla.evaluar(ciclos(600))).hasValueSatisfying(d -> {
            assertThat(d.causa()).isEqualTo(Causa.SONDA_SIN_DATOS);
            assertThat(d.evidencia()).contains("10 min");
        });
    }

    @Test
    void justoEnElLimiteSigueSiendoReciente() {
        assertThat(regla.evaluar(ciclos(300))).isEmpty();
    }

    @Test
    void unSegundoPasadoElLimiteAvisa() {
        assertThat(regla.evaluar(ciclos(301))).hasValueSatisfying(d -> {
            assertThat(d.causa()).isEqualTo(Causa.SONDA_SIN_DATOS);
            assertThat(d.evidencia()).contains("5 min");
        });
    }

    @Test
    void listaVaciaAvisa() {
        assertThat(regla.evaluar(List.of())).hasValueSatisfying(d -> {
            assertThat(d.causa()).isEqualTo(Causa.SONDA_SIN_DATOS);});
    }

    @Test
    void unCicloIncompletoRecienteNoCuenta() {
        List<Medicion> soloRouter = List.of(m(AHORA.minusSeconds(10).toString(), "router", true));

        assertThat(regla.evaluar(unir(ciclos(600), soloRouter))).hasValueSatisfying(d -> {
            assertThat(d.causa()).isEqualTo(Causa.SONDA_SIN_DATOS);
            assertThat(d.evidencia()).contains("10 min");
        });
    }

    @Test
    void mandaElUltimoCicloNoElPrimero() {
        assertThat(regla.evaluar(unir(ciclos(600), ciclos(15)))).isEmpty();
    }

}
