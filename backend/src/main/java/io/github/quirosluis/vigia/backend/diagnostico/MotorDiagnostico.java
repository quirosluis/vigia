package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

public final class MotorDiagnostico {

    private final List<Regla> reglas;           // en orden de prioridad

    public MotorDiagnostico(List<Regla> reglas) {
        if (reglas == null || reglas.isEmpty()) {
            throw new IllegalArgumentException("el motor necesita al menos una regla");
        }
        this.reglas = List.copyOf(reglas);                // copia inmutable: nadie la cambia desde fuera
    }

    /** Las reglas reales en su orden. Un solo sitio donde se decide la prioridad. */
    public static MotorDiagnostico estandar(Clock reloj) {
        return new MotorDiagnostico(List.of(
                new ReglaSondaSinDatos(reloj),
                new ReglaProveedorInternet(),
                new ReglaSinProblemas() // la más "optimista", la última
                ));
    }

    public Diagnostico diagnosticar(List<Medicion> mediciones) {
        for (Regla regla : reglas) {
            Optional<Diagnostico> diagnostico = regla.evaluar(mediciones);
            if (diagnostico.isPresent()) {
                return diagnostico.get();                           // la primera que decide, manda
            }
        }

        return new Diagnostico(Causa.INDETERMINADO,
                "ninguna regla concluyente con " + mediciones.size() + " mediciones");
    }
}
