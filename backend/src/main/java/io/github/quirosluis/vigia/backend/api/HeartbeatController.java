package io.github.quirosluis.vigia.backend.api;

import io.github.quirosluis.vigia.backend.ingesta.IngestaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/heartbeats")

public class HeartbeatController {

    private final IngestaService servicio;

    public HeartbeatController(IngestaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)

    public void recibir(@Valid @RequestBody LoteRequest lote) {
        servicio.guardar(lote);
    }

}
