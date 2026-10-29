package io.github.quirosluis.vigia.backend.ingesta;

import io.github.quirosluis.vigia.backend.api.LoteRequest;
import io.github.quirosluis.vigia.backend.api.MedicionRequest;
import io.github.quirosluis.vigia.backend.persistencia.MedicionEntity;
import io.github.quirosluis.vigia.backend.persistencia.MedicionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestaService {

    private final MedicionRepository repositorio;

    public IngestaService(MedicionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public int guardar(LoteRequest lote) {
        List<MedicionEntity> entidades = lote.mediciones().stream()
                .map(MedicionRequest::aMedicion)
                .map(m -> new MedicionEntity(m.timestampUtc(), lote.sitio(),
                        lote.sonda(), m.objetivo(), m.ok(), m.latenciaMs(), m.bateria(),
                        m.cargando()))
                .toList();
        repositorio.saveAll(entidades);
        return entidades.size();
    }
}
