package com.voo.api.controller;

import com.voo.api.domain.model.Aeroporto;
import com.voo.api.domain.model.Aerovia;
import com.voo.api.dto.request.CriarAeroviaRequest;
import com.voo.api.service.AeroportoService;
import com.voo.api.service.AeroviaService;
import com.voo.api.service.DisponibilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aerovias")
@Tag(name = "Aerovias", description = "Gerenciamento de aerovias")
public class AeroviaController {

    private final AeroviaService aeroviaService;
    private final AeroportoService aeroportoService;
    private final DisponibilidadeService disponibilidadeService;

    public AeroviaController(AeroviaService aeroviaService, AeroportoService aeroportoService,
                             DisponibilidadeService disponibilidadeService) {
        this.aeroviaService = aeroviaService;
        this.aeroportoService = aeroportoService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as aerovias")
    public List<Aerovia> listar() {
        return aeroviaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aerovia por identificador")
    public Aerovia buscar(@PathVariable String id) {
        return aeroviaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova aerovia")
    public Aerovia criar(@Valid @RequestBody CriarAeroviaRequest req) {
        Aeroporto origem = aeroportoService.buscar(req.getOrigemCodigo().toUpperCase());
        Aeroporto destino = aeroportoService.buscar(req.getDestinoCodigo().toUpperCase());
        Aerovia aerovia = new Aerovia();
        aerovia.setId(req.getId().toUpperCase());
        aerovia.setOrigem(origem);
        aerovia.setDestino(destino);
        aerovia.setDistancia(req.getDistancia());
        return aeroviaService.criar(aerovia);
    }

    @GetMapping("/{id}/altitudes-disponiveis")
    @Operation(summary = "Consultar altitudes disponíveis em uma aerovia para um período")
    public Map<String, Object> altitudesDisponiveis(
            @PathVariable String id,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        List<Integer> altitudes = disponibilidadeService.altitudesDisponiveisAerovia(id, inicio, fim);
        return Map.of("aerovia", id, "inicio", inicio, "fim", fim, "altitudesDisponiveis", altitudes);
    }
}
