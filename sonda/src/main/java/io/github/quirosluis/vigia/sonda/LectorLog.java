package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.MedicionParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LectorLog {

    private static final System.Logger LOG = System.getLogger(LectorLog.class.getName());

    private LectorLog(){}


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
