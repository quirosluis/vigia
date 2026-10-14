package io.github.quirosluis.vigia;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static java.lang.Integer.parseInt;

public final class MedicionParser {

    private static final int MAX_BATERIA = 100;
    private static final int MIN_BATERIA = 0;

    public static Medicion parsear(String linea) {
        String[] campos = linea.split(",", -1);
        if (campos.length !=6) {
            throw new IllegalArgumentException(
                    "Se esperaban 6 campos y hay "+campos.length+": "+linea);
        }
        Instant timestampUtc = parsearInstant("timestampUtc",campos[0]);
        String objetivo = validaInformado("objetivo",campos[1]);
        boolean ok = parsearBooleano("ok",campos[2]);
        Integer latenciaMs;
        if(ok){
            latenciaMs = parsearEntero("latenciaMs",campos[3]);
        } else {
            //Para latencia no OK, se ignora latencia, siempre null.
            latenciaMs = null;
        }
        int bateria = parsearEntero("bateria",campos[4]);
        validaBateria(bateria);
        boolean cargando = parsearBooleano("cargando", campos[5]);
        return new Medicion(timestampUtc, objetivo, ok, latenciaMs, bateria, cargando);
    }

    private static void validaBateria(int bateria) {

        if ( bateria > MAX_BATERIA|| bateria < MIN_BATERIA) {
            throw new IllegalArgumentException("El campo bateria está fuera de rango ("+MIN_BATERIA+"-"+MAX_BATERIA+"): "+bateria);
        }
    }

    private static String validaInformado(String nombreCampo, String campo) {
        if(campo.isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo "+nombreCampo+" no está informado");
        }
        return campo;
    }

    private static Instant parsearInstant(String nombreCampo, String campo) {
        validaInformado(nombreCampo,campo);
        try {
            return Instant.parse(campo);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El campo "+nombreCampo+" no es un Instant: '"+campo+"'",e);
        }
    }
    private static int parsearEntero(String nombreCampo, String campo) {
        validaInformado(nombreCampo,campo);
        try {
            return parseInt(campo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo "+nombreCampo+" no es un int: '"+campo+"'",e);
        }
    }

    private static boolean parsearBooleano(String nombreCampo, String campo) {
        validaInformado(nombreCampo,campo);
        if (!("1".equals(campo) || "0".equals(campo))){
            throw new IllegalArgumentException(
                    "El campo "+nombreCampo+" no es un boolean:'"+campo+"'");
        }

        return "1".equals(campo);
    }
}
