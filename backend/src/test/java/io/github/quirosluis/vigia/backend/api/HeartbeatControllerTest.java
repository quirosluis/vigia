package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HeartbeatControllerTest {

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

    @Test
    void loteEsValido2registros() {
        var l = loteValido();
        var lMil = new LoteRequest(l.sitio(),l.sonda(), Collections.nCopies(2, medicionValida()));
        assertThat(validator.validate(lMil)).isEmpty();
    }
}