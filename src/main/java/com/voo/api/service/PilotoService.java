package com.voo.api.service;

import com.voo.api.domain.model.Piloto;
import com.voo.api.domain.repository.PilotoRepository;
import com.voo.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PilotoService {

    private final PilotoRepository pilotoRepository;

    public PilotoService(PilotoRepository pilotoRepository) {
        this.pilotoRepository = pilotoRepository;
    }

    public List<Piloto> listar() {
        return pilotoRepository.findAll();
    }

    public Piloto buscar(Long id) {
        return pilotoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Piloto não encontrado: " + id));
    }

    public Piloto criar(Piloto piloto) {
        if (pilotoRepository.findByRegistro(piloto.getRegistro()).isPresent()) {
            throw new IllegalArgumentException("Registro de piloto já existe: " + piloto.getRegistro());
        }
        return pilotoRepository.save(piloto);
    }
}
