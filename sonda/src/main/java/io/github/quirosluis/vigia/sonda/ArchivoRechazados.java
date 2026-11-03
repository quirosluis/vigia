package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.MedicionFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public final class ArchivoRechazados {
    private static final System.Logger LOG = System.getLogger(ArchivoRechazados.class.getName());

    private final Path carpeta;

    public ArchivoRechazados(Path carpeta) {
        this.carpeta = carpeta;
    }

    public void guardar(List<Medicion> trozo) {                      // sin throws: así cabe en un Consumer
        if (trozo.isEmpty()) {
            return;
        }
        LocalDate dia = trozo.get(0).timestampUtc().atZone(ZoneOffset.UTC).toLocalDate();
        Path fichero = carpeta.resolve("rechazados-" + dia + ".log");
        String texto = trozo.stream()
                .map(MedicionFormatter::formatear)
                .collect(Collectors.joining("\n", "", "\n"));      // una línea por medición, acaba en salto de línea
        try {
            Files.createDirectories(carpeta);
            Files.writeString(fichero, texto, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            LOG.log(System.Logger.Level.WARNING, "{0} mediciones rechazadas guardadas en {1}", trozo.size(), fichero);
        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "No se pudieron guardar los rechazados: {0}", e.getMessage());
        }
    }
}
