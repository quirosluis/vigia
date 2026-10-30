package io.github.quirosluis.vigia.backend.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface MedicionRepository extends JpaRepository<MedicionEntity, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO medicion (sitio, sonda, timestamp_utc, objetivo, ok, latencia_ms, bateria, cargando)
            VALUES (:sitio, :sonda, :timestamp_utc, :objetivo, :ok, :latencia_ms, :bateria, :cargando)
            ON CONFLICT (sitio, sonda, timestamp_utc, objetivo) DO NOTHING
            """, nativeQuery = true)
    int insertarSiNoExiste(@Param("sitio") String sitio,
                           @Param("sonda") String sonda,
                           @Param("timestamp_utc")Instant timestamp_utc,
                           @Param("objetivo") String objetivo,
                           @Param("ok") Boolean ok,
                           @Param("latencia_ms") Integer latencia_ms,
                           @Param("bateria") int bateria,
                           @Param("cargando") Boolean cargando);



}
