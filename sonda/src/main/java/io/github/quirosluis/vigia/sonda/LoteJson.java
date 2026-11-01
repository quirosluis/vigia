package io.github.quirosluis.vigia.sonda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.quirosluis.vigia.Medicion;

import java.util.List;

public final class LoteJson {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private LoteJson() {
    }

    public static String aJson(String sitio, String sonda, List<Medicion> mediciones) {

        List<MedicionEnvio> envio = mediciones.stream()
                .map(m -> new MedicionEnvio(m.timestampUtc().toString(), m.objetivo(), m.ok(), m.latenciaMs(), m.bateria(), m.cargando()))
                .toList();

        try {
            return MAPPER.writeValueAsString(new LoteEnvio(sitio, sonda, envio));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el lote", e);
        }
    }
}
