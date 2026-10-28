package com.voo.api.controller;

import com.voo.api.domain.model.Piloto;
import com.voo.api.dto.request.CriarPilotoRequest;
import com.voo.api.service.PilotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pilotos")
@Tag(name = "Pilotos", description = "Gerenciamento de pilotos")
public class PilotoController {

    private final PilotoService pilotoService;

    public PilotoController(PilotoService pilotoService) {
        this.pilotoService = pilotoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os pilotos")
    public List<Piloto> listar() {
        return pilotoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar piloto por ID")
    public Piloto buscar(@PathVariable Long id) {
        return pilotoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo piloto")
    public Piloto criar(@Valid @RequestBody CriarPilotoRequest req) {
        Piloto piloto = new Piloto();
        piloto.setRegistro(req.getRegistro());
        piloto.setNome(req.getNome());
        return pilotoService.criar(piloto);
    }
}
