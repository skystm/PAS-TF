package com.voo.api.service;

import com.voo.api.domain.model.Aeroporto;
import com.voo.api.domain.repository.AeroportoRepository;
import com.voo.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeroportoService {

    private final AeroportoRepository aeroportoRepository;

    public AeroportoService(AeroportoRepository aeroportoRepository) {
        this.aeroportoRepository = aeroportoRepository;
    }

    public List<Aeroporto> listar() {
        return aeroportoRepository.findAll();
    }

    public Aeroporto buscar(String codigo) {
        return aeroportoRepository.findById(codigo)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto não encontrado: " + codigo));
    }

    public Aeroporto criar(Aeroporto aeroporto) {
        if (aeroportoRepository.existsById(aeroporto.getCodigo())) {
            throw new IllegalArgumentException("Aeroporto já existe: " + aeroporto.getCodigo());
        }
        return aeroportoRepository.save(aeroporto);
    }
}
