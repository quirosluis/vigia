package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static io.github.quirosluis.vigia.backend.diagnostico.CiclosDePrueba.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MotorDiagnosticoTest {

    // Reglas falsas con lambdas: no miran las mediciones
    private static final Regla NO_OPINA = mediciones -> Optional.empty();

    private static Regla decide(Causa causa) {
        return mediciones -> Optional.of(new Diagnostico(causa, "regla de prueba"));
    }

    @Test
    void laPrimeraReglaQueOpinaManda() {
        MotorDiagnostico motor = new MotorDiagnostico(List.of(
                NO_OPINA,
                decide(Causa.PROVEEDOR_INTERNET),
                decide(Causa.SIN_PROBLEMAS)));

        assertThat(motor.diagnosticar(List.of()).causa()).isEqualTo(Causa.PROVEEDOR_INTERNET);
    }

    @Test
    void lasReglasPosterioresNoSeEvaluan() {
        Regla prohibida = mediciones -> { throw new AssertionError("no debía evaluarse"); };
        MotorDiagnostico motor = new MotorDiagnostico(List.of(
                decide(Causa.PROVEEDOR_INTERNET), prohibida));

        assertThat(motor.diagnosticar(List.of()).causa()).isEqualTo(Causa.PROVEEDOR_INTERNET);
    }

    @Test
    void siNingunaOpinaEsIndeterminado() {
        MotorDiagnostico motor = new MotorDiagnostico(List.of(NO_OPINA, NO_OPINA));

        assertThat(motor.diagnosticar(List.of()).causa()).isEqualTo(Causa.INDETERMINADO);
    }

    @Test
    void sinReglasNoSePuedeCrear() {
        assertThatThrownBy(() -> new MotorDiagnostico(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---- Con las reglas reales (estandar): la prioridad importa ----

    static final Instant AHORA = Instant.parse("2026-11-25T18:00:00Z");
    private final MotorDiagnostico estandar = MotorDiagnostico.estandar(Clock.fixed(AHORA, ZoneOffset.UTC));

    private static String hace(long segundos) {
        return AHORA.minusSeconds(segundos).toString();
    }

    @Test
    void ciclosBuenosPeroViejosNoSonSinProblemas() {
        List<Medicion> viejos = unir(
                ciclo(hace(7215), true, true, true),
                ciclo(hace(7200), true, true, true));

        assertThat(estandar.diagnosticar(viejos).causa()).isEqualTo(Causa.SONDA_SIN_DATOS);   // no SIN_PROBLEMAS
    }

    @Test
    void ciclosBuenosRecientesSonSinProblemas() {
        List<Medicion> recientes = unir(
                ciclo(hace(30), true, true, true),
                ciclo(hace(15), true, true, true));

        assertThat(estandar.diagnosticar(recientes).causa()).isEqualTo(Causa.SIN_PROBLEMAS);
    }

    @Test
    void internetCaidoRecienteEsProveedor() {
        List<Medicion> sinInternet = unir(
                ciclo(hace(30), true, false, true),
                ciclo(hace(15), true, false, true));

        assertThat(estandar.diagnosticar(sinInternet).causa()).isEqualTo(Causa.PROVEEDOR_INTERNET);
    }
}