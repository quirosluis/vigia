package io.github.quirosluis.vigia.sonda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoteJsonTest {

    @Test
    void generaElContratoDelBackend() throws Exception {

        List<Medicion> mediciones = List.of(
                new Medicion(Instant.parse("2026-11-01T08:05:00Z"), "router", true, 4, 80, true),
                new Medicion(Instant.parse("2026-11-01T08:05:00Z"), "internet", false, null, 80, true));

        String json = LoteJson.aJson("sede", "sonda1", mediciones);

        JsonNode raiz = new ObjectMapper().readTree(json);
        JsonNode lista = raiz.get("mediciones");

        assertThat(raiz.get("sitio").asText()).isEqualTo("sede");
        assertThat(raiz.get("sonda").asText()).isEqualTo("sonda1");
        assertThat(lista.size()).isEqualTo(2);
        assertThat(lista.get(0).get("timestampUtc").asText()).isEqualTo("2026-11-01T08:05:00Z");
        assertThat(lista.get(0).get("latenciaMs").asInt()).isEqualTo(4);
        assertThat(lista.get(1).get("latenciaMs").isNull()).isTrue();
    }


}
