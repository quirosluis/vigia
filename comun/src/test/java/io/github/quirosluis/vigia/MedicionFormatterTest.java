package io.github.quirosluis.vigia;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MedicionFormatterTest {

    @Test
    void validaFormateoOkTrue() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", true, 160, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);
        Medicion m1 = MedicionParser.parsear(logMedicion);
        assertThat(m1).isEqualTo(m);
    }

    @Test
    void formateaLineaConOkFalsoSinLatencia() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, 160, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);

        assertThat(logMedicion).isEqualTo("2026-10-10T18:42:07Z,Router,0,,80,0");
    }

    @Test
    void validaFormateoConTexto() {
        String texto = "2026-10-10T18:42:07Z,router,1,3,87,1";
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "router", true, 3, 87, true);
        String logMedicion = MedicionFormatter.formatear(m);

        assertThat(logMedicion).isEqualTo(texto);
    }

    @Test
    void idaYVueltaConOkFalso() {
        Medicion m = new Medicion(Instant.parse("2026-10-10T18:42:07Z"), "Router", false, null, 80, false);
        String logMedicion = MedicionFormatter.formatear(m);
        Medicion m1 = MedicionParser.parsear(logMedicion);
        assertThat(m1).isEqualTo(m);
    }

}