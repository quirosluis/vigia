package io.github.quirosluis.vigia.backend.ingesta;

import io.github.quirosluis.vigia.Medicion;
import io.github.quirosluis.vigia.backend.api.LoteRequest;
import io.github.quirosluis.vigia.backend.api.MedicionRequest;
import io.github.quirosluis.vigia.backend.persistencia.MedicionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestaService {

    private static final Logger log = LoggerFactory.getLogger(IngestaService.class);

    private final MedicionRepository repositorio;

    public IngestaService(MedicionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public int guardar(LoteRequest lote) {
        int nuevas = 0;
        for (MedicionRequest r : lote.mediciones()) {
            Medicion m = r.aMedicion();                    // valida y normaliza (segunda capa de defensa)
            nuevas += repositorio.insertarSiNoExiste(lote.sitio(), lote.sonda(),
                    m.timestampUtc(), m.objetivo(), m.ok(),
                    m.latenciaMs(), m.bateria(), m.cargando());
        }
        log.info("Lote {}/{}: {} nuevas de {} recibidas",
                lote.sitio(), lote.sonda(), nuevas, lote.mediciones().size());
        return nuevas;
    }
}
