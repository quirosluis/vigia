package io.github.quirosluis.vigia.sonda;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class EstadoEnvioTest {
    @TempDir
    Path carpeta;

    private EstadoEnvio estado() {
        return new EstadoEnvio(carpeta.resolve("estado.txt"));
    }

    @Test
    void sinFicheroNoHayMarca() throws IOException {
        assertThat(estado().ultimoEnviado()).isEmpty();
    }

    @Test
    void loGuardadoSeLee() throws IOException {
        Instant marca = Instant.parse("2026-11-01T08:10:15Z");

        estado().guardar(marca);

        assertThat(estado().ultimoEnviado()).contains(marca);   // objeto nuevo: lee del fichero
    }

    @Test
    void guardarOtraVezSustituyeLaMarca() throws IOException {
        EstadoEnvio e = estado();
        e.guardar(Instant.parse("2026-11-01T08:10:15Z"));
        e.guardar(Instant.parse("2026-11-01T08:10:30Z"));

        assertThat(estado().ultimoEnviado()).contains(Instant.parse("2026-11-01T08:10:30Z"));
    }

    @Test
    void noDejaElTemporalAtras() throws IOException {
        estado().guardar(Instant.parse("2026-11-01T08:10:15Z"));

        assertThat(carpeta.resolve("estado.txt.tmp")).doesNotExist();
    }

    @Test
    void unFicheroConBasuraSeTrataComoSinEstado() throws IOException {
        Files.writeString(carpeta.resolve("estado.txt"), "esto no es una fecha");
        assertThat(estado().ultimoEnviado()).isEmpty();
    }

    @Test
    void unFicheroVacioSeTrataComoSinEstado() throws IOException {
        Files.writeString(carpeta.resolve("estado.txt"), "");

        assertThat(estado().ultimoEnviado()).isEmpty();
    }
}
