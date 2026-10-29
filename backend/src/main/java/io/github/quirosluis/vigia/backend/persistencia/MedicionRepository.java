package io.github.quirosluis.vigia.backend.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicionRepository extends JpaRepository<MedicionEntity, Long> {
}
