package io.github.quirosluis.vigia.sonda;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class LectorLogTest {
    @TempDir
    Path carpeta;                                    // JUnit crea una carpeta temporal y la borra al acabar

    private Path fichero(String contenido) throws IOException {
        return Files.writeString(carpeta.resolve("mediciones.log"), contenido);
    }

    /*        2026-10-19T23:59:33Z,dns,1,15,92,0
        2026-10-19T23:59:48Z,router,0,,92,0
        2026-10-19T23:59:48Z,internet,1,12,92,0
        2026-10-19T23:59:48Z,dns,1,14,92,0*/


    @Test
    void leeLasLineasCompletas() throws IOException {
        Path f = fichero("2026-10-19T23:59:33Z,dns,1,15,92,0\n2026-10-19T23:59:48Z,router,0,,92,0\n");

        assertThat(LectorLog.leer(f)).hasSize(2);
    }

    @Test
    void ignoraLaUltimaLineaSiEstaIncompleta() throws IOException {
        Path f = fichero("2026-10-19T23:59:48Z,router,0,,92,0\n 2026-10-19T23:59:48Z,inte");   // sin salto de línea final

        assertThat(LectorLog.leer(f)).hasSize(1);
    }

    @Test
    void unaLineaCorruptaNoBloqueaLasDemas() throws IOException {
        Path f = fichero("2026-10-19T23:59:33Z,dns,1,15,92,0\nesto no es una línea\n2026-10-19T23:59:48Z,router,0,,92,0\n");

        assertThat(LectorLog.leer(f)).hasSize(2);
    }
}
