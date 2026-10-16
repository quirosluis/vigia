package io.github.quirosluis.vigia.sonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class BateriaParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private BateriaParser() { }

    public static EstadoBateria parsear(String json) {
        if (json == null) {
            throw new IllegalArgumentException("El JSON de BateriaJson es null");
        }

        BateriaJson datos;

        try {
            datos = MAPPER.readValue(json, BateriaJson.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON de BateriaJson no válido: '" + json + "'", e);
        }

        if (datos.percentage() == null) {
            throw new IllegalArgumentException("Falta el campo percentage: '" + json + "'");
        }

        if (datos.percentage() < 0 || datos.percentage() > 100) {
            throw new IllegalArgumentException("Falta el campo percentage fuera de rango 0-100: '" + json + "'");
        }

        if (datos.plugged() == null) {
            throw new IllegalArgumentException("Falta el campo plugged: '" + json + "'");
        }

        boolean cargando = switch (datos.plugged()) {
            case "PLUGGED_AC", "PLUGGED_USB", "PLUGGED_WIRELESS" -> true;
            case "UNPLUGGED" -> false;
            default -> throw new IllegalArgumentException(
                    "Valor de plugged desconocido: '" + datos.plugged() + "'");
        };

        return new EstadoBateria(datos.percentage(), cargando);

    }
}
