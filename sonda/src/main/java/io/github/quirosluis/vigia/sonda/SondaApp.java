package io.github.quirosluis.vigia.sonda;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.MedicionFormatter;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        if (args.length != 1 && args.length != 4) {
            System.err.println("Uso: java -jar sonda.jar <ip-router> [<url-backend> <sitio> <sonda>]");
            System.exit(1);
        }
        String ipRouter = args[0];

        if (args.length == 4) {
            URI destino = URI.create(args[1]);                         // una URL mal escrita falla aquí, al arrancar
            Enviador enviador = new EnviadorLotes(destino, args[2], args[3]);
            EstadoEnvio estadoEnvio = new EstadoEnvio(CARPETA_DATOS.resolve("estado-envio.txt"));
            ArchivoRechazados rechazados = new ArchivoRechazados(CARPETA_DATOS);
            Despachador despachador = new Despachador(estadoEnvio, enviador, rechazados::guardar);
            System.out.println("Envío activado hacia " + destino);

            ScheduledExecutorService planificadorEnvio = Executors.newSingleThreadScheduledExecutor();

            planificadorEnvio.scheduleWithFixedDelay(() -> {
                try {
                    List<Medicion> pendientes = LectorLog.leerDesde(
                            CARPETA_DATOS, estadoEnvio.ultimoEnviado(), LocalDate.now(ZoneOffset.UTC));
                    System.out.println(Instant.now() + " Envío: " + pendientes.size() + " mediciones leídas");
                    despachador.enviarPendientes(pendientes);
                } catch (Exception e) {
                    System.err.println(Instant.now() + " Error en el envío: " + e);
                }
            }, 30, 60, TimeUnit.SECONDS);

        }

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
