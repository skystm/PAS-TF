package com.voo.api.service;

import com.voo.api.domain.model.Aerovia;
import com.voo.api.domain.repository.AeroviaRepository;
import com.voo.api.domain.repository.AeroportoRepository;
import com.voo.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeroviaService {

    private final AeroviaRepository aeroviaRepository;
    private final AeroportoRepository aeroportoRepository;

    public AeroviaService(AeroviaRepository aeroviaRepository, AeroportoRepository aeroportoRepository) {
        this.aeroviaRepository = aeroviaRepository;
        this.aeroportoRepository = aeroportoRepository;
    }

    public List<Aerovia> listar() {
        return aeroviaRepository.findAll();
    }

    public Aerovia buscar(String id) {
        return aeroviaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia não encontrada: " + id));
    }

    public Aerovia criar(Aerovia aerovia) {
        if (aeroviaRepository.existsById(aerovia.getId())) {
            throw new IllegalArgumentException("Aerovia já existe: " + aerovia.getId());
        }
        if (!aeroportoRepository.existsById(aerovia.getOrigem().getCodigo())) {
            throw new RecursoNaoEncontradoException("Aeroporto origem não encontrado");
        }
        if (!aeroportoRepository.existsById(aerovia.getDestino().getCodigo())) {
            throw new RecursoNaoEncontradoException("Aeroporto destino não encontrado");
        }
        return aeroviaRepository.save(aerovia);
    }
}
