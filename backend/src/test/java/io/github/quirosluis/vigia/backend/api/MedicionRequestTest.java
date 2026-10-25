package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MedicionRequestTest {

    private static Validator validator;

    @BeforeAll
    static void creaValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static MedicionRequest medicionValida() {
        return new MedicionRequest(Instant.parse("2026-10-10T18:42:07Z"), "router", true, 132, 83, true);
    }

    private static List<String> camposConError(MedicionRequest r) {
        return validator.validate(r).stream()
                .map(v -> v.getPropertyPath().toString())
                .sorted()
                .toList();
    }

    @Test
    void aceptaMedicionValida() {
        assertThat(validator.validate(medicionValida())).isEmpty();
    }

    @Test
    void rechazaTimestampUtcNoInformado(){
        var m = medicionValida();
        var mError = new MedicionRequest(null,m.objetivo(), m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("timestampUtc");
    }

    @Test
    void rechazaObjetivoNull(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),null, m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaObjetivoEspacios(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),"   ", m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaObjetivoNoInformado(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),"", m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaObjetivoConSaltoDeLinea(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo()+"\n", m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaObjetivoConRetornoDeCarro(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo()+"\r", m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaObjetivoConComa(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo()+",", m.ok(), m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("objetivo");
    }

    @Test
    void rechazaOkNoInformado(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), null, m.latenciaMs(), m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("ok");
    }

    @Test
    void rechazaLatenciaMsNegativa(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), -1, m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("latenciaMs");
    }

    @Test
    void aceptaLatenciaCero(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), 0, m.bateria(), m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }

    @Test
    void rechazaOkTrueSinLatencia(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), true, null, m.bateria(), m.cargando());
        assertThat(camposConError(mError)).containsExactly("latenciaCoherente");
    }

    @Test
    void aceptaOkFalseSinLatencia(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), false, null, m.bateria(), m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }

    @Test
    void aceptaOkTrueConLatenciaCero(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), true, 0, m.bateria(), m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }

    @Test
    void rechazaBateriaNull(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), null, m.cargando());
        assertThat(camposConError(mError)).containsExactly("bateria");
    }

    @Test
    void rechazaBateriaBajoRango(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), -1, m.cargando());
        assertThat(camposConError(mError)).containsExactly("bateria");
    }

    @Test
    void rechazaBateriaSobreRango(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), 101, m.cargando());
        assertThat(camposConError(mError)).containsExactly("bateria");
    }

    @Test
    void aceptaBateriaCero(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), 0, m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }

    @Test
    void aceptaBateriaCien(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), 100, m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }

    @Test
    void rechazaCargandoNull(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(), m.ok(), m.latenciaMs(), m.bateria(), null);
        assertThat(camposConError(mError)).containsExactly("cargando");
    }

    @Test
    void aceptarOkFalseConLatenciaInformada(){
        var m = medicionValida();
        var mError = new MedicionRequest(m.timestampUtc(),m.objetivo(),false, m.latenciaMs(), 100, m.cargando());
        assertThat(camposConError(mError)).isEmpty();
    }
}
