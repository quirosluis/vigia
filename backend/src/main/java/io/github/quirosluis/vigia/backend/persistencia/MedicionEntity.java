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
@Table(name = "medicion")
public class MedicionEntity {
    // Instant timestampUtc, String objetivo, boolean ok, Integer latenciaMs, int bateria, boolean cargando
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private Instant timestampUtc;

    @Column(nullable = false)
    private String sitio;

    @Column(nullable = false)
    private String sonda;

    @Column(nullable = false)
    private String objetivo;

    @Column(nullable = false)
    private boolean ok;

    @Column(nullable = true)
    private Integer latenciaMs;

    @Column(nullable = false)
    private int bateria;

    @Column(nullable = false)
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
