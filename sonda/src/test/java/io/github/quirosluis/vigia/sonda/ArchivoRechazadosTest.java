package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoRechazadosTest {


    @TempDir
    Path carpeta;

    private static Medicion medicion(String marca, String objetivo) {
        return new Medicion(Instant.parse(marca), objetivo, true, 180, 56, false);
    }

    private Path ficheroDelDia() {
        return carpeta.resolve("rechazados-2026-11-03.log");
    }

    @Test
    void guardaEnElFicheroDelDiaDeLaPrimeraMedicion() throws IOException {
        new ArchivoRechazados(carpeta).guardar(List.of(medicion("2026-11-03T10:00:00Z", "uno")));

        assertThat(Files.readAllLines(ficheroDelDia())).hasSize(1);
    }

    @Test
    void dosLotesDelMismoDiaSeAnadenSinPisarse() throws IOException {
        ArchivoRechazados archivo = new ArchivoRechazados(carpeta);

        archivo.guardar(List.of(medicion("2026-11-03T10:00:00Z", "uno")));
        archivo.guardar(List.of(medicion("2026-11-03T11:00:00Z", "dos")));

        List<String> lineas = Files.readAllLines(ficheroDelDia());
        assertThat(lineas).hasSize(2);
        assertThat(lineas.get(0)).contains("uno");
        assertThat(lineas.get(1)).contains("dos");
    }

    @Test
    void loGuardadoSePuedeVolverALeer() throws IOException {
        List<Medicion> trozo = List.of(
                medicion("2026-11-03T10:00:00Z", "uno"),
                medicion("2026-11-03T10:00:15Z", "dos"));

        new ArchivoRechazados(carpeta).guardar(trozo);

        assertThat(LectorLog.leer(ficheroDelDia())).isEqualTo(trozo);
    }

    @Test
    void unTrozoVacioNoCreaFichero() throws IOException {
        new ArchivoRechazados(carpeta).guardar(List.of());

        try (var ficheros = Files.list(carpeta)) {
            assertThat(ficheros).isEmpty();
        }
    }

}
