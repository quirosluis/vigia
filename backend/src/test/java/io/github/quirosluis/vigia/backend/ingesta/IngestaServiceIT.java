package io.github.quirosluis.vigia.backend.ingesta;

import io.github.quirosluis.vigia.backend.api.LoteRequest;
import io.github.quirosluis.vigia.backend.api.MedicionRequest;
import io.github.quirosluis.vigia.backend.persistencia.MedicionEntity;
import io.github.quirosluis.vigia.backend.persistencia.MedicionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/vigia_test",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class IngestaServiceIT {

    private static final Instant T0 = Instant.parse("2026-10-26T18:00:00Z");

    @Autowired
    private IngestaService servicio;

    @Autowired
    private MedicionRepository repositorio;

    @BeforeEach
    void limpia() {
        repositorio.deleteAll();
    }

    private static MedicionRequest medicion(Instant ts, String objetivo, boolean ok, Integer latenciaMs) {
        return new MedicionRequest(ts, objetivo, ok, latenciaMs, 80, true);
    }

    private static LoteRequest lote(MedicionRequest... mediciones) {
        return new LoteRequest("demo", "movil-1", List.of(mediciones));
    }

    @Test
    void reenviarElMismoLoteNoDuplicaNiSiHayLatenciaNula() {
        LoteRequest lote = lote(
                medicion(T0, "router", true, 4),
                medicion(T0, "internet", false, null),       // el caso que falló el 30/10
                medicion(T0, "dns", true, 17));

        int primera = servicio.guardar(lote);
        int segunda = servicio.guardar(lote);

        assertThat(primera).isEqualTo(3);
        assertThat(segunda).isZero();
        assertThat(repositorio.count()).isEqualTo(3);
    }

    @Test
    void unSolapeParcialInsertaSoloLasNuevas() {
        servicio.guardar(lote(
                medicion(T0, "router", true, 4),
                medicion(T0, "internet", false, null)));

        int nuevas = servicio.guardar(lote(
                medicion(T0, "router", true, 4),                       // ya estaba
                medicion(T0, "internet", false, null),                 // ya estaba
                medicion(T0, "dns", true, 17),                         // nueva
                medicion(T0.plusSeconds(15), "router", true, 5)));     // nueva

        assertThat(nuevas).isEqualTo(2);
        assertThat(repositorio.count()).isEqualTo(4);
    }

    @Test
    void ganaLaPrimeraMedicion() {
        servicio.guardar(lote(medicion(T0, "router", true, 10)));
        servicio.guardar(lote(medicion(T0, "router", true, 99)));    // misma clave, otro valor

        assertThat(repositorio.findAll())
                .singleElement()
                .extracting(MedicionEntity::getLatenciaMs)
                .isEqualTo(10);
    }

    @Test
    void unaClaveRepetidaDentroDelMismoLoteNoFalla() {
        servicio.guardar(lote(
                medicion(T0, "router", true, 10),
                medicion(T0, "router", true, 99)));                  // el mismo lote la repite

        assertThat(repositorio.count()).isEqualTo(1);
    }

    @Test
    void conOkFalseLaLatenciaSeGuardaComoNula() {
        servicio.guardar(lote(medicion(T0, "internet", false, 7)));   // ok=false con latencia informada

        assertThat(repositorio.findAll())
                .singleElement()
                .extracting(MedicionEntity::getLatenciaMs)
                .isNull();
    }
}