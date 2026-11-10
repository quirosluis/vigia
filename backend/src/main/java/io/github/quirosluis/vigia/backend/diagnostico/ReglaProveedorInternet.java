package io.github.quirosluis.vigia.backend.diagnostico;

import io.github.quirosluis.vigia.Medicion;

import java.util.List;
import java.util.Optional;

public final class ReglaProveedorInternet implements Regla {

    @Override
    public Optional<Diagnostico> evaluar(List<Medicion> mediciones) {

        Optional<List<List<Medicion>>> ventana = VentanaCiclos.ultimos(mediciones);
        if (ventana.isEmpty()) {
            return Optional.empty();
        }
        List<List<Medicion>> ciclos = ventana.get();

        if (!ciclos.stream().allMatch(ReglaProveedorInternet::routerOkInternetKo)) {
            return Optional.empty();
        }
        return Optional.of(new Diagnostico(Causa.PROVEEDOR_INTERNET, ciclos.size()+" ciclos completos conexión a internet KO"));
    }

    private static boolean routerOkInternetKo(List<Medicion> ciclo) {
        boolean routerOk = ciclo.stream()
                .anyMatch(m -> m.objetivo().equals("router") && m.ok());
        boolean internetKo = ciclo.stream()
                .anyMatch(m -> m.objetivo().equals("internet") && !m.ok());
        return  routerOk && internetKo;
    }

}
