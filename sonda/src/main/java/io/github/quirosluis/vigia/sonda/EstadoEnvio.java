package io.github.quirosluis.vigia.sonda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class EstadoEnvio {
    private final Path fichero;

    private static final System.Logger LOG = System.getLogger(EstadoEnvio.class.getName());

    public EstadoEnvio(Path fichero) {
        this.fichero = fichero;
    }

    public Optional<Instant> ultimoEnviado() throws IOException {
        if (!Files.exists(fichero)) {
            return Optional.empty();
        }
        String texto = Files.readString(fichero).trim();

        if(texto.isEmpty()) {
            return Optional.empty();
        }else{
            try {
                return  Optional.of(Instant.parse(texto));
            } catch (DateTimeParseException e) {
                LOG.log(System.Logger.Level.WARNING,
                        "Fichero de estado ilegible; se reenviará desde el principio ({0})", e.getMessage());
                return Optional.empty();
            }
        }

    }

    public void guardar(Instant marca) throws IOException {
        Path temporal = fichero.resolveSibling(fichero.getFileName() + ".tmp");
        Files.writeString(temporal, marca.toString());
        Files.move(temporal, fichero, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

}
