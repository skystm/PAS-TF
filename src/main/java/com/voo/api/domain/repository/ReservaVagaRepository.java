package com.voo.api.domain.repository;

import com.voo.api.domain.model.ReservaVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaVagaRepository extends JpaRepository<ReservaVaga, Long> {

    List<ReservaVaga> findByPlanoVooId(Long planoVooId);

    @Query("SELECT r FROM ReservaVaga r WHERE r.aeroporto.codigo = :aeroportoCodigo " +
           "AND r.numeroVaga = :numeroVaga " +
           "AND r.slotInicio < :fim AND r.slotFim > :inicio")
    List<ReservaVaga> findConflitos(
        @Param("aeroportoCodigo") String aeroportoCodigo,
        @Param("numeroVaga") int numeroVaga,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT r FROM ReservaVaga r WHERE r.aeroporto.codigo = :aeroportoCodigo " +
           "AND r.slotInicio >= :inicio AND r.slotFim <= :fim")
    List<ReservaVaga> findByAeroportoAndPeriodo(
        @Param("aeroportoCodigo") String aeroportoCodigo,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}
