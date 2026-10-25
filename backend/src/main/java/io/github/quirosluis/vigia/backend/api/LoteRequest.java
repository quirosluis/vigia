package io.github.quirosluis.vigia.backend.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LoteRequest(

        @NotBlank String sitio,
        @NotBlank String sonda,
        @NotEmpty @Size(max = 1000) List<@Valid MedicionRequest> mediciones
) {
}
