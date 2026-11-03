package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.MedicionParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LectorLog {

    private static final System.Logger LOG = System.getLogger(LectorLog.class.getName());

    private LectorLog(){}

    public static List<Medicion> leerDesde(Path carpeta, Optional<Instant> ultima, LocalDate hoy) throws IOException
    {
        List<Medicion> mediciones = new ArrayList<>();
        if (ultima.isEmpty()) {                                     // sin estado: todos los ficheros, por orden
            try (Stream<Path> ficheros = Files.list(carpeta)) {
                List<Path> todos = ficheros
                        .filter(f -> f.getFileName().toString().matches("mediciones-\\d{4}-\\d{2}-\\d{2}\\.log"))
                        .sorted()
                        .toList();
                for (Path f : todos) {
                    mediciones.addAll(LectorLog.leer(f));
                }
            }
            return mediciones;
        }

        LocalDate dia = ultima.get().atZone(ZoneOffset.UTC).toLocalDate();   // el día de la última marca
        for (; !dia.isAfter(hoy); dia = dia.plusDays(1)) {                    // hasta hoy, INCLUIDO
            Path fichero = carpeta.resolve("mediciones-" + dia + ".log");         // LocalDate.toString() = AAAA-MM-DD
            if (Files.exists(fichero)) {                                      // un día sin fichero no es un error
                mediciones.addAll(LectorLog.leer(fichero));
            }
        }
        return mediciones;


    }


    public static List<Medicion> leer(Path fichero) throws IOException {

        String contenidoLog = Files.readString(fichero);
        List<String> lineas = contenidoLog.lines().toList();

        //Si la última línea está incompleta, no la tratamos
        if(!contenidoLog.isEmpty() && !contenidoLog.endsWith("\n")) {
            lineas = lineas.subList(0,lineas.size()-1);
        }

        List<Medicion> mediciones =  new ArrayList<>();
        for (int i = 0; i <lineas.size();i++) {
            String linea = lineas.get(i);
            if(linea.isBlank()){
                //ignoramos la línea en blanco
                continue;
            }
            try {
                mediciones.add(MedicionParser.parsear(linea));
            } catch (IllegalArgumentException  e) {
                LOG.log(System.Logger.Level.WARNING, "Línea {0} ignorada: {1}", i + 1, e.getMessage());
            }
        }
        return mediciones;

    }
}
