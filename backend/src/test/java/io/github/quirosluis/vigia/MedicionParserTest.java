package io.github.quirosluis.vigia;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MedicionParserTest {

    @Test
    void parseaLineaValida() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:07Z,Router,1,132,83,1");

        assertThat(m.timestampUtc()).isEqualTo("2026-10-10T18:42:07Z");
        assertThat(m.cargando()).isTrue();
        assertThat(m.latenciaMs()).isEqualTo(132);
    }

    @Test
    void rechazaLineaConPocosCampos() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:07Z,Router"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("campos");
    }

    @Test
    void rechazaLineaConMuchosCampos() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:07Z,Router,1,200,55,1,2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("campos");
    }

    @Test
    void parseaLineaCorrecta() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:07Z,router,1,3,87,1");
        assertThat(m.latenciaMs()).isEqualTo(3);
        assertThat(m.ok()).isTrue();
    }

    @Test
    void comprobarLatenciaNullParaLineaKO() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:12Z,internet,0,250,87,1");
        assertThat(m.latenciaMs()).isNull();
        assertThat(m.ok()).isFalse();
    }

    @Test
    void comprobarCampoOKBooleanNoValido() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:07Z,router,2,3,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("campo ok no es un boolean");
    }

    @Test
    void comprobarCampoLatenciaNoEsIntValidoVacio() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo latenciaMs no está informado");
    }

    @Test
    void comprobarCampoLantenciaNoEsIntValido() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,abc,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latenciaMs")
                .hasCauseInstanceOf(NumberFormatException.class);
    }

    @Test
    void comprobarCampoTimeStampNoEsInstant() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-kk-10T18:42:12Z,internet,1,130,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("campo timestampUtc no es un Instant");
    }

    @Test
    void comprobarCampoBateriaFueraDeRango() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,130,120,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo bateria está fuera de rango");
    }

    @Test
    void comprobarIndicadorCargaEsBoolean() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,130,20,7"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo cargando no es un boolean");
    }

    @Test
    void comprobarIndicadorCargaVacio() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,130,20,"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo cargando no está informado");
    }

    @Test
    void comprobarCampoBateriaNoValidoLimiteInferior() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,130,-1,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo bateria está fuera de rango");
    }

    @Test
    void comprobarCampoBateriaNoValidoLimiteSuperior() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,130,101,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo bateria está fuera de rango");
    }

    @Test
    void comprobarCampoBateriaLimiteInferior() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,120,0,1");
        assertThat(m.bateria()).isEqualTo(0);
    }

    @Test
    void comprobarCampoBateriaLimiteSuperior() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:12Z,internet,1,120,100,1");
        assertThat(m.bateria()).isEqualTo(100);
    }


    @Test
    void comprobarlatenciaMsEsNulaParaLineaKO() {
        Medicion m = MedicionParser.parsear("2026-10-10T18:42:12Z,internet,0,,87,1");
        assertThat(m.latenciaMs()).isNull();
    }

    @Test
    void comprobarTimestampUtcNoInformado() {
        assertThatThrownBy(() -> MedicionParser.parsear(",router,1,300,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El campo timestampUtc no está informado");
    }

    @Test
    void comprobarObjetivoNoInformado() {
        assertThatThrownBy(() -> MedicionParser.parsear("2026-10-10T18:42:12Z,,1,300,87,1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("campo objetivo no está informado");
    }
}