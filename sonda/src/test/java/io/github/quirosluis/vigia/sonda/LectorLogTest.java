package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LectorLogTest {
    @TempDir
    Path carpeta;                                    // JUnit crea una carpeta temporal y la borra al acabar

    private Path fichero(String contenido, String nombreFichero) throws IOException {
        return Files.writeString(carpeta.resolve(nombreFichero), contenido);
    }

    /*        2026-10-19T23:59:33Z,dns,1,15,92,0
        2026-10-19T23:59:48Z,router,0,,92,0
        2026-10-19T23:59:48Z,internet,1,12,92,0
        2026-10-19T23:59:48Z,dns,1,14,92,0*/


    @Test
    void leeLasLineasCompletas() throws IOException {
        Path f = fichero("2026-10-19T23:59:33Z,dns,1,15,92,0\n2026-10-19T23:59:48Z,router,0,,92,0\n", "mediciones.log");

        assertThat(LectorLog.leer(f)).hasSize(2);
    }

    @Test
    void ignoraLaUltimaLineaSiEstaIncompleta() throws IOException {
        Path f = fichero("2026-10-19T23:59:48Z,router,0,,92,0\n 2026-10-19T23:59:48Z,inte", "mediciones.log");   // sin salto de línea final

        assertThat(LectorLog.leer(f)).hasSize(1);
    }

    @Test
    void unaLineaCorruptaNoBloqueaLasDemas() throws IOException {
        Path f = fichero("2026-10-19T23:59:33Z,dns,1,15,92,0\nesto no es una línea\n2026-10-19T23:59:48Z,router,0,,92,0\n", "mediciones.log");

        assertThat(LectorLog.leer(f)).hasSize(2);
    }

    //2026-11-02T10:00:00Z,dia2,1,4,80,1

    @Test
    void sinEstadoLeeTodosLosFicherosDeDias() throws IOException {
        fichero("2026-11-01T10:00:00Z,dia1,1,4,80,1\n","mediciones-2026-11-01.log");
        fichero("2026-11-02T10:00:00Z,dia2,1,4,80,1\n","mediciones-2026-11-02.log");
        fichero("cuchufleta", "nosoyMedicion.txt");                                        // no es un fichero de días: se ignora

        List<Medicion> mediciones = LectorLog.leerDesde(carpeta, Optional.empty(), LocalDate.parse("2026-11-02"));

        assertThat(mediciones).extracting(Medicion::objetivo).containsExactly("dia1","dia2");
    }
    @Test
    void conEstadoNoLeeLosDiasAnteriores() throws IOException {
        fichero("2026-11-01T10:00:00Z,dia1,1,4,80,1\n", "mediciones-2026-11-01.log");
        fichero("2026-11-02T10:00:00Z,dia2,1,4,80,1\n", "mediciones-2026-11-02.log");
        fichero("2026-11-03T10:00:00Z,dia3,1,4,80,1\n", "mediciones-2026-11-03.log");

        List<Medicion> mediciones = LectorLog.leerDesde(carpeta,
                Optional.of(Instant.parse("2026-11-02T10:00:00Z")), LocalDate.parse("2026-11-03"));

        assertThat(mediciones).extracting(Medicion::objetivo).containsExactly("dia2", "dia3");
    }

    @Test
    void cruzaLaMedianoche() throws IOException {
        fichero("2026-11-02T23:59:45Z,dia2,1,4,80,1\n", "mediciones-2026-11-02.log");
        fichero("2026-11-03T00:00:00Z,dia3,1,4,80,1\n", "mediciones-2026-11-03.log");

        List<Medicion> mediciones = LectorLog.leerDesde(carpeta,
                Optional.of(Instant.parse("2026-11-02T23:59:45Z")), LocalDate.parse("2026-11-03"));

        assertThat(mediciones).extracting(Medicion::objetivo).containsExactly("dia2", "dia3");
    }

    @Test
    void unDiaSinFicheroNoFalla() throws IOException {
        fichero("2026-11-01T10:00:00Z,dia1,1,4,80,1\n", "mediciones-2026-11-01.log");

        List<Medicion> mediciones = LectorLog.leerDesde(carpeta,
                Optional.of(Instant.parse("2026-11-01T10:00:00Z")), LocalDate.parse("2026-11-03"));

        assertThat(mediciones).extracting(Medicion::objetivo).containsExactly("dia1");
    }


}
