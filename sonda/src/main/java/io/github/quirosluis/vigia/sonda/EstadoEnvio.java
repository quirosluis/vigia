package io.github.quirosluis.vigia.sonda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;

public class EstadoEnvio {
    private final Path fichero;

    public EstadoEnvio(Path fichero) {
        this.fichero = fichero;
    }

    public Optional<Instant> ultimaEnviada() throws IOException {
        if (!Files.exists(fichero)) {
            return Optional.empty();
        }
        String texto = Files.readString(fichero).trim();
        return texto.isEmpty() ? Optional.empty() : Optional.of(Instant.parse(texto));
    }

    public void guardar(Instant marca) throws IOException {
        Path temporal = fichero.resolveSibling(fichero.getFileName() + ".tmp");
        Files.writeString(temporal, marca.toString());
        Files.move(temporal, fichero, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

}
