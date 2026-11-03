package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class Despachador {

    private static final int MAX_LOTE = 1000;

    private final EstadoEnvio estado;
    private final Enviador enviador;
    private final Consumer<List<Medicion>> alRechazar;

    public Despachador(EstadoEnvio estado, Enviador enviador, Consumer<List<Medicion>> alRechazar) {
        this.estado = estado;
        this.enviador = enviador;
        this.alRechazar = alRechazar;
    }

    public void enviarPendientes(List<Medicion> mediciones) throws IOException {
        Optional<Instant> ultimo = estado.ultimoEnviado();

        List<Medicion> pendientes = mediciones.stream()
                .filter(m -> ultimo.map(u -> !m.timestampUtc().isBefore(u)).orElse(true)) //desde la última marca Incluida
                .sorted(Comparator.comparing(Medicion::timestampUtc))
                .toList();

        for (int i = 0; i < pendientes.size(); i += MAX_LOTE) {
            List<Medicion> chunk = pendientes.subList(i, Math.min(i + MAX_LOTE, pendientes.size()));
            Enviador.Resultado resultado = enviador.enviar(chunk);
            if (resultado == Enviador.Resultado.REINTENTAR) {
                return;
            }
            if (resultado == Enviador.Resultado.RECHAZADO) { alRechazar.accept(chunk); }

            estado.guardar(ultimaMarca(chunk));
        }
    }

    private static Instant ultimaMarca(List<Medicion> chunk) {
        return chunk.get(chunk.size() - 1).timestampUtc();
    }

}
