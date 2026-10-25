package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record MedicionRequest(

        @NotNull Instant timestampUtc,
        @NotBlank @Pattern(regexp = "[^,\\r\\n]*") String objetivo,
        @NotNull Boolean ok,
        @PositiveOrZero Integer latenciaMs,
        @NotNull @Max(100) @Min(0) Integer bateria,
        @NotNull Boolean cargando

) {
    @AssertTrue(message = "latenciaMs es obligatoria cuando ok es true")
    public boolean isLatenciaCoherente() {
        if (ok == null) {
            return true;   // ya lo reporta @NotNull sobre ok
        }
        return !ok || latenciaMs != null;
    }
}
