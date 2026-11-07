package io.github.quirosluis.vigia.backend.diagnostico;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiagnosticoTest {

    @Test
    void aceptaUnDiagnosticoCompleto() {
        Diagnostico r = new Diagnostico(Causa.CORTE_ELECTRICO, "se detecta corte de luz");

        assertThat(r.causa()).isEqualTo(Causa.CORTE_ELECTRICO);
    }

    @Test
    void rechazaUnMotivoNulo() {
        assertThatThrownBy(() -> new Diagnostico(null, "algo"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaUnaEvidenciaEnBlanco() {
        assertThatThrownBy(() -> new Diagnostico(Causa.CORTE_ELECTRICO, "  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unaReglaPuedeSerUnaLambda() {
        Regla nadaQueDecir = ventana -> Optional.empty();   // un solo método abstracto

        assertThat(nadaQueDecir.evaluar(null)).isEmpty();
    }

}
