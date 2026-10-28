package com.voo.api.domain.repository;

import com.voo.api.domain.model.Aerovia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AeroviaRepository extends JpaRepository<Aerovia, String> {
    List<Aerovia> findByOrigemCodigo(String origemCodigo);
    List<Aerovia> findByDestinoCodigo(String destinoCodigo);
}
