import io.github.quirosluis.vigia.backend.api.HeartbeatController;
import io.github.quirosluis.vigia.backend.ingesta.IngestaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(HeartbeatController.class)
class HeartbeatControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private IngestaService servicio;

    @Test
    void loteValidoLlamaAlServicioYDevuelve202() {
        assertThat(mvc.post().uri("/api/heartbeats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sitio":"torre","sonda":"sonda1","mediciones":[
                          {"timestampUtc":"2026-10-10T18:42:07Z","objetivo":"router","ok":true,"latenciaMs":165,"bateria":95,"cargando":true},
                          {"timestampUtc":"2026-10-10T18:42:07Z","objetivo":"internet","ok":true,"latenciaMs":12,"bateria":95,"cargando":true}]}
                        """)
                .exchange()).hasStatus(HttpStatus.ACCEPTED);

        verify(servicio).guardar(any());
    }

    @Test
    void loteSinSitioDevuelve400YNoLlamaAlServicio() {
        assertThat(mvc.post().uri("/api/heartbeats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sonda":"sonda1","mediciones":[
                          {"timestampUtc":"2026-10-10T18:42:07Z","objetivo":"router","ok":true,"latenciaMs":165,"bateria":95,"cargando":true}]}
                        """)
                .exchange())
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errores[0].campo").isEqualTo("sitio");

        verifyNoInteractions(servicio);
    }
}