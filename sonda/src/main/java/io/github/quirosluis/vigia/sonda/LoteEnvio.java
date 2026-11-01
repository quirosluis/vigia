package io.github.quirosluis.vigia.sonda;

import java.util.List;

public record LoteEnvio(String sitio, String sonda, List<MedicionEnvio> mediciones) {
}
