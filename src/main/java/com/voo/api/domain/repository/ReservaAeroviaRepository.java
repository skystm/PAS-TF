package com.voo.api.domain.repository;

import com.voo.api.domain.model.ReservaAerovia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaAeroviaRepository extends JpaRepository<ReservaAerovia, Long> {

    List<ReservaAerovia> findByPlanoVooId(Long planoVooId);

    @Query("SELECT r FROM ReservaAerovia r WHERE r.aerovia.id = :aeroviaId AND r.altitude = :altitude " +
           "AND r.slotInicio < :fim AND r.slotFim > :inicio")
    List<ReservaAerovia> findConflitos(
        @Param("aeroviaId") String aeroviaId,
        @Param("altitude") int altitude,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT r FROM ReservaAerovia r WHERE r.aerovia.id = :aeroviaId " +
           "AND r.slotInicio >= :inicio AND r.slotFim <= :fim")
    List<ReservaAerovia> findByAeroviaAndPeriodo(
        @Param("aeroviaId") String aeroviaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}
