package io.github.quirosluis.vigia.backend.persistencia;


import jakarta.persistence.*;

import java.time.Instant;

/*
MedicionEntity: una columna por campo de Medicion,
 más el identificador del sitio y de la sonda (vienen en el envoltorio del lote).
  Tipos: Instant para la marca de tiempo (UTC, como decidiste),
   Integer para la latencia (puede ser null) y boolean para ok y cargando.
 */
@Entity
@Table(name = "medicion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_medicion",
                columnNames = {"sitio", "sonda", "timestamp_utc", "objetivo"}))

public class MedicionEntity {
    // Instant timestampUtc, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "timestamp_utc", nullable = false)
    private Instant timestampUtc;

    @Column(name = "sitio", nullable = false)
    private String sitio;

    @Column(name = "sonda", nullable = false)
    private String sonda;

    @Column(name = "objetivo", nullable = false)
    private String objetivo;

    @Column(name = "ok", nullable = false)
    private boolean ok;

    @Column(name = "latencia_ms", nullable = true)
    private Integer latenciaMs;

    @Column(name = "bateria", nullable = false)
    private int bateria;

    @Column(name = "cargando", nullable = false)
    private boolean cargando;

    protected MedicionEntity() {
    }

    public MedicionEntity(Instant timestampUtc, String sitio, String sonda, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando) {
        this.timestampUtc = timestampUtc;
        this.sitio = sitio;
        this.sonda = sonda;
        this.objetivo = objetivo;
        this.ok = ok;
        this.latenciaMs = latenciaMs;
        this.bateria = bateria;
        this.cargando = cargando;
    }

    public Long getId() {
        return id;
    }

    public Instant getTimestampUtc() {
        return timestampUtc;
    }

    public String getSitio() {
        return sitio;
    }

    public String getSonda() {
        return sonda;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public boolean isOk() {
        return ok;
    }

    public Integer getLatenciaMs() {
        return latenciaMs;
    }

    public int getBateria() {
        return bateria;
    }

    public boolean isCargando() {
        return cargando;
    }
}
