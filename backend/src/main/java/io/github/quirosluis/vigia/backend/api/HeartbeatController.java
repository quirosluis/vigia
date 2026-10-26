package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/heartbeats")

public class HeartbeatController {
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recibir(@Valid @RequestBody LoteRequest lote)
    {
        //Aquí llamaremos para guardar el lote
    }

}
