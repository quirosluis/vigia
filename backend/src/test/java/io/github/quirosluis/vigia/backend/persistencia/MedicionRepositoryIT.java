package io.github.quirosluis.vigia.backend.persistencia;

import io.github.quirosluis.vigia.Medicion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {                       // ¡contra vigia_test, nunca contra vigia!
        "spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/vigia_test",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MedicionRepositoryIT {

    @Autowired
    MedicionRepository repositorio;

    static final Instant T0 = Instant.parse("2026-11-26T10:00:00Z");

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();                     // cada test parte de cero (en vigia_test)
    }

    private void guardar(String sitio, String sonda, long segundos, String objetivo, boolean ok) {
        repositorio.save(new MedicionEntity(
                T0.plusSeconds(segundos), sitio, sonda, objetivo, ok, ok ? 5 : null, 80, true));
    }

    private List<MedicionEntity> leer(String sitio, String sonda) {
        return repositorio.findBySitioAndSondaAndTimestampUtcAfterOrderByTimestampUtcAsc(sitio, sonda, T0);
    }

    private List<Instant> marcas(List<MedicionEntity> leidos) {
        return leidos.stream().map(e -> e.aMedicion().timestampUtc()).toList();
    }

    @Test
    void soloLasPosterioresAlInstanteYEnOrden() {
        guardar("casa", "movil-oneplus", 30, "router", true);   // insertadas desordenadas a propósito
        guardar("casa", "movil-oneplus", 0, "router", true);    // justo en T0: "After" la excluye
        guardar("casa", "movil-oneplus", 15, "router", true);
        guardar("casa", "movil-oneplus", -60, "router", true);  // anterior

        assertThat(marcas(leer("casa", "movil-oneplus")))
                .containsExactly(T0.plusSeconds(15), T0.plusSeconds(30));
    }

    @Test
    void ignoraOtroSitioYOtraSonda() {
        guardar("casa", "movil-oneplus", 10, "router", true);
        guardar("segunda-residencia", "movil-oneplus", 10, "router", true);
        guardar("casa", "otra-sonda", 10, "router", true);

        List<MedicionEntity> leidos = leer("casa", "movil-oneplus");

        assertThat(leidos).hasSize(1);
        assertThat(leidos.get(0).getSitio()).isEqualTo("casa");
        assertThat(leidos.get(0).getSonda()).isEqualTo("movil-oneplus");
    }

    @Test
    void sinDatosDevuelveListaVacia() {
        assertThat(leer("casa", "movil-oneplus")).isEmpty();
    }

    @Test
    void okFalseLlegaAlDominioConLatenciaNula() {
        guardar("casa", "movil-oneplus", 10, "internet", false);

        Medicion m = leer("casa", "movil-oneplus").get(0).aMedicion();

        assertThat(m.objetivo()).isEqualTo("internet");
        assertThat(m.ok()).isFalse();
        assertThat(m.latenciaMs()).isNull();
    }
}