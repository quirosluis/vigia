package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DespachadorTest {
    @TempDir
    Path carpeta;

    private final List<List<Medicion>> enviados = new ArrayList<>();

    private EstadoEnvio estado() {
        return new EstadoEnvio(carpeta.resolve("estado.txt"));
    }

    private static Medicion medicion(String marca) {
        return new Medicion(Instant.parse(marca), "router", true, 4, 80, true);
    }

    @Test
    void conUnEnvioCorrectoAvanzaElEstado() throws IOException {
        Despachador d = new Despachador(estado(), trozo -> {
            enviados.add(trozo);
            return Enviador.Resultado.ENVIADO;
        });

        d.enviarPendientes(List.of(medicion("2026-11-02T10:00:00Z"), medicion("2026-11-02T10:00:15Z")));

        assertThat(estado().ultimoEnviado()).contains(Instant.parse("2026-11-02T10:00:15Z"));
    }

    @Test
    void siFallaElEnvioElEstadoNoAvanza() throws IOException {
        Despachador d = new Despachador(estado(), trozo -> Enviador.Resultado.REINTENTAR);

        d.enviarPendientes(List.of(medicion("2026-11-02T10:00:00Z")));

        assertThat(estado().ultimoEnviado()).isEmpty();
    }

    @Test
    void laSiguienteVezReenviaDesdeLaUltimaMarcaIncluida() throws IOException {
        estado().guardar(Instant.parse("2026-11-02T10:00:15Z"));
        Despachador d = new Despachador(estado(), trozo -> {
            enviados.add(trozo);
            return Enviador.Resultado.ENVIADO;
        });

        d.enviarPendientes(List.of(
                medicion("2026-11-02T10:00:00Z"),        // anterior: no se envía
                medicion("2026-11-02T10:00:15Z"),        // misma marca: SÍ se envía otra vez
                medicion("2026-11-02T10:00:30Z")));

        //assertThat(enviados).singleElement().satisfies(trozo -> assertThat(trozo).hasSize(2));

        assertThat(enviados).singleElement()
                .satisfies(trozo -> assertThat(trozo)
                        .extracting(Medicion::timestampUtc)
                        .containsExactly(Instant.parse("2026-11-02T10:00:15Z"),
                                Instant.parse("2026-11-02T10:00:30Z")));
    }

}
