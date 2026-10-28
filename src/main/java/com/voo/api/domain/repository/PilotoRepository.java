package com.voo.api.domain.repository;

import com.voo.api.domain.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PilotoRepository extends JpaRepository<Piloto, Long> {
    Optional<Piloto> findByRegistro(String registro);
}
