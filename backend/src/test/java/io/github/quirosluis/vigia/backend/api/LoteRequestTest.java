package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoteRequestTest {

    private static Validator validator;

    @BeforeAll
    static void creaValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static MedicionRequest medicionValida() {
        return new MedicionRequest(Instant.parse("2026-10-10T18:42:07Z"), "router", true, 132, 83, true);
    }

    private static LoteRequest loteValido() {
        return new LoteRequest("torre", "sonda1", List.of(medicionValida()));
    }

    private static List<String> camposConError(LoteRequest l) {
        return validator.validate(l).stream()
                .map(v -> v.getPropertyPath().toString())
                .sorted()
                .toList();
    }

    @Test
    void aceptaLoteValido() {
        assertThat(validator.validate(loteValido())).isEmpty();
    }

    @Test
    void rechazaLoteSitioNoInformado(){
        var l = loteValido();
        var lError = new LoteRequest("",l.sonda(),l.mediciones());
        assertThat(camposConError(lError)).containsExactly("sitio");
    }

    @Test
    void rechazaLoteSondaNoInformada(){
        var l = loteValido();
        var lError = new LoteRequest(l.sitio(),"",l.mediciones());
        assertThat(camposConError(lError)).containsExactly("sonda");
    }

    @Test
    void rechazaMedicionNoValida(){
        var l = loteValido();
        var m = medicionValida();
        var me = new MedicionRequest(null,m.objetivo(),m.ok(),m.latenciaMs(),m.bateria(),m.cargando());
        var lError = new LoteRequest(l.sitio(),l.sonda(),List.of(medicionValida(), medicionValida(), me,  medicionValida()));
        assertThat(camposConError(lError)).containsExactly("mediciones[2].timestampUtc");
    }

    @Test
    void rechazaDosMedicionesNoValidas(){
        var l = loteValido();
        var m = medicionValida();
        var me = new MedicionRequest(null,m.objetivo(),m.ok(),m.latenciaMs(),m.bateria(),m.cargando());
        var me2 = new MedicionRequest(m.timestampUtc(),"",m.ok(),m.latenciaMs(),m.bateria(),m.cargando());
        var lError = new LoteRequest(l.sitio(),l.sonda(),List.of(medicionValida(), medicionValida(), me,  medicionValida(), me2));
        assertThat(camposConError(lError)).containsExactly("mediciones[2].timestampUtc","mediciones[4].objetivo");
    }

    @Test
    void rechazaMedicionesSuperaLimite(){
        var l = loteValido();
        var lError = new LoteRequest(l.sitio(),l.sonda(), Collections.nCopies(1001, medicionValida()));
        assertThat(camposConError(lError)).containsExactly("mediciones");
    }

    @Test
    void aceptaMilMediciones() {
        var l = loteValido();
        var lMil = new LoteRequest(l.sitio(),l.sonda(), Collections.nCopies(1000, medicionValida()));
        assertThat(validator.validate(lMil)).isEmpty();
    }

    @Test
    void rechazaMedicionesNull(){
        var l = loteValido();
        var lError = new LoteRequest(l.sitio(),l.sonda(),null);
        assertThat(camposConError(lError)).containsExactly("mediciones");
    }

    @Test
    void rechazaMedicionesVacio(){
        var l = loteValido();
        var lError = new LoteRequest(l.sitio(),l.sonda(),List.of());
        assertThat(camposConError(lError)).containsExactly("mediciones");
    }
}
