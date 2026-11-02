package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;

import java.util.List;

public interface Enviador {
    enum Resultado {ENVIADO, RECHAZADO, REINTENTAR}

    Resultado enviar(List<Medicion> trozo);
}
