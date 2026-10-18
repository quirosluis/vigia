package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.MedicionFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SondaApp {
    private static final long INTERVALO_SEGUNDOS = 15;
    private static final Path CARPETA_DATOS = Path.of("datos");

    private SondaApp() {
    }

    public static void main(String[] args) {
        // Guarda: el dato sensible llega por argumento, no está en el código.
        if (args.length != 1) {
            System.err.println("Uso: java -jar sonda.jar <ip-router>");
            System.exit(1);                    // código distinto de 0 = terminó con error
        }
        String ipRouter = args[0];

        ScheduledExecutorService planificador = Executors.newSingleThreadScheduledExecutor();
        planificador.scheduleAtFixedRate(() -> {
            try {
                ciclo(ipRouter);
            } catch (Exception e) {
                // Frontera exterior: un fallo puntual no puede matar el bucle.
                System.err.println(Instant.now() + " Error en el ciclo: " + e);
            }
        }, 0, INTERVALO_SEGUNDOS, TimeUnit.SECONDS);

        System.out.println("Sonda en marcha cada " + INTERVALO_SEGUNDOS + " s");
    }

    private static void ciclo(String ipRouter) throws IOException, InterruptedException {
        Instant ahora = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        EstadoBateria estado = LectorBateria.leer();

        registrar(ahora, "router", Pinger.medir(ipRouter), estado);
        registrar(ahora, "internet", Pinger.medir("1.1.1.1"), estado);
        registrar(ahora, "dns", Pinger.medir("www.google.com"), estado);
    }

    private static void registrar(Instant momento, String etiqueta, PingResult ping, EstadoBateria bateria) throws IOException {
        Medicion medicion = new Medicion(momento, etiqueta, ping.ok(), ping.latenciaMs(), bateria.porcentaje(), bateria.cargando());
        String logMedicion = MedicionFormatter.formatear(medicion);
        escribir(momento, logMedicion);
    }

    private static void escribir(Instant momento, String linea) throws IOException {
        Path fichero = CARPETA_DATOS.resolve(
                "mediciones-" + LocalDate.ofInstant(momento, ZoneOffset.UTC) + ".log");
        Files.createDirectories(CARPETA_DATOS);
        Files.writeString(fichero, linea + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

}
