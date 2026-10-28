package com.voo.api.service;

import com.voo.api.domain.model.Aeronave;
import com.voo.api.domain.repository.AeronaveRepository;
import com.voo.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeronaveService {

    private final AeronaveRepository aeronaveRepository;

    public AeronaveService(AeronaveRepository aeronaveRepository) {
        this.aeronaveRepository = aeronaveRepository;
    }

    public List<Aeronave> listar() {
        return aeronaveRepository.findAll();
    }

    public Aeronave buscar(String prefixo) {
        return aeronaveRepository.findById(prefixo)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aeronave não encontrada: " + prefixo));
    }

    public Aeronave criar(Aeronave aeronave) {
        if (aeronaveRepository.existsById(aeronave.getPrefixo())) {
            throw new IllegalArgumentException("Aeronave já existe: " + aeronave.getPrefixo());
        }
        return aeronaveRepository.save(aeronave);
    }
}
