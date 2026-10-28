package com.voo.api.controller;

import com.voo.api.domain.model.Aeroporto;
import com.voo.api.dto.request.CriarAeroportoRequest;
import com.voo.api.dto.request.ConsultaDisponibilidadeRequest;
import com.voo.api.service.AeroportoService;
import com.voo.api.service.DisponibilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aeroportos")
@Tag(name = "Aeroportos", description = "Gerenciamento de aeroportos")
public class AeroportoController {

    private final AeroportoService aeroportoService;
    private final DisponibilidadeService disponibilidadeService;

    public AeroportoController(AeroportoService aeroportoService, DisponibilidadeService disponibilidadeService) {
        this.aeroportoService = aeroportoService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os aeroportos")
    public List<Aeroporto> listar() {
        return aeroportoService.listar();
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar aeroporto por código")
    public Aeroporto buscar(@PathVariable String codigo) {
        return aeroportoService.buscar(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo aeroporto")
    public Aeroporto criar(@Valid @RequestBody CriarAeroportoRequest req) {
        Aeroporto aeroporto = new Aeroporto();
        aeroporto.setCodigo(req.getCodigo().toUpperCase());
        aeroporto.setNome(req.getNome());
        aeroporto.setVagasEstacionamento(req.getVagasEstacionamento());
        return aeroportoService.criar(aeroporto);
    }

    @GetMapping("/{codigo}/vagas-disponiveis")
    @Operation(summary = "Consultar vagas disponíveis em um aeroporto para um período")
    public Map<String, Object> vagasDisponiveis(
            @PathVariable String codigo,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        List<Integer> vagas = disponibilidadeService.vagasDisponiveisAeroporto(codigo, inicio, fim);
        return Map.of("aeroporto", codigo, "inicio", inicio, "fim", fim, "vagasDisponiveis", vagas);
    }
}
