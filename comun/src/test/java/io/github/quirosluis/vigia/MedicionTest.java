package io.github.quirosluis.vigia;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MedicionTest {

    @Test
    void rechazaLatenciaNulaConOkTrue() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", true, null, 80, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latenciaMs");
    }
    @Test
    void rechazaTimeStampUtcNulo() {
        assertThatThrownBy(() -> new Medicion(null, "Router", true, 160, 80, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("timestampUtc");
    }
    @Test
    void rechazaObjetivoNulo() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"), null, true, 160, 80, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("objetivo");
    }
    @Test
    void rechazaObjetivoBlanco() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z")," ", true, 160, 80, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("objetivo");
    }
    @Test
    void rechazaBateriaBajoRango() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router", true, 160, -1, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bateria");
    }
    @Test
    void rechazaBateriaSobreRango() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router", true, 160, 101, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bateria");
    }
    @Test
    void aceptaBateriaCero() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 160, 0, false);
        assertThat(m.bateria()).isEqualTo(0);
    }
    @Test
    void aceptaBateriaCien() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 160, 100, false);
        assertThat(m.bateria()).isEqualTo(100);
    }
    @Test
    void rechazaOkTrueyLatenciaNegativa() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router", true, -1, 18, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latenciaMs");
    }
    @Test
    void aceptaOkTrueyLatenciaCero() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", true, 0, 80, false);
        assertThat(m.latenciaMs()).isEqualTo(0);
    }
    @Test
    void normalizaLatenciaNullParaOkFalse() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 250, 80, false);
        assertThat(m.latenciaMs()).isNull();
    }
    @Test
    void rechazaObjetivoCaracterInvalidoR() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router\r", true, 120, 18, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("objetivo");
    }
    @Test
    void rechazaObjetivoCaracterInvalidoN() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router\n", true, 120, 18, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("objetivo");
    }
    @Test
    void rechazaObjetivoCaracterInvalidoComa() {
        assertThatThrownBy(() -> new Medicion(Instant.parse("2026-10-10T18:42:07Z"),"Router,", true, 120, 18, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("objetivo");
    }
    @Test
    void paraOkFalseRetornaLatenciaNull() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, null, 80, false);
        assertThat(m.latenciaMs()).isNull();
    }
}
