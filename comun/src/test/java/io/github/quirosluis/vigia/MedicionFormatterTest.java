package io.github.quirosluis.vigia;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MedicionFormatterTest {

    @Test
    void validaFormateoOkTrue() {
        //Instant timestampUtc = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", true, 160, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);
        Medicion m1 = MedicionParser.parsear(logMedicion);
        assertThat(m1).isEqualTo(m);
    }

    @Test
    void idaYVueltaIgnoraLatenciaCuandoNoOk() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 160, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);
        Medicion m1 = MedicionParser.parsear(logMedicion);
        assertThat(m1.latenciaMs()).isNull();
        assertThat(m1.ok()).isFalse();
    }

    @Test
    void idaYVueltaIgnoraLatenciaCuandoNoOkLinea() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 160, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);

        assertThat(logMedicion).isEqualTo("2026-10-10T18:42:07Z,Router,0,,80,0");
    }

    @Test
    void validaFormateoLatenciaNull() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, null, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);
        Medicion m1 = MedicionParser.parsear(logMedicion);
        assertThat(m1).isEqualTo(m);
    }

    @Test
    void validaFormateoLatenciaNullCuandoOKTrue() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", true, null, 80, false);
        assertThatThrownBy(() -> MedicionFormatter.formatear(m))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latenciaMs");
    }

    @Test
    void validaFormateoConTexto() {
        String texto = "2026-10-10T18:42:07Z,router,1,3,87,1";
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "router", true, 3, 87, true);
        String logMedicion = MedicionFormatter.formatear(m);

        assertThat(logMedicion).isEqualTo(texto);
    }

}