package com.voo.api.domain.repository;

import com.voo.api.domain.model.PlanoVoo;
import com.voo.api.domain.model.SituacaoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanoVooRepository extends JpaRepository<PlanoVoo, Long> {
    List<PlanoVoo> findBySituacao(SituacaoPlano situacao);
}
