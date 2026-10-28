package com.voo.api.controller;

import com.voo.api.domain.model.*;
import com.voo.api.dto.request.*;
import com.voo.api.service.AeronaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aeronaves")
@Tag(name = "Aeronaves", description = "Gerenciamento de aeronaves")
public class AeronaveController {

    private final AeronaveService aeronaveService;

    public AeronaveController(AeronaveService aeronaveService) {
        this.aeronaveService = aeronaveService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as aeronaves")
    public List<Aeronave> listar() {
        return aeronaveService.listar();
    }

    @GetMapping("/{prefixo}")
    @Operation(summary = "Buscar aeronave por prefixo")
    public Aeronave buscar(@PathVariable String prefixo) {
        return aeronaveService.buscar(prefixo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova aeronave (tipo informado no campo 'tipo': PARTICULAR, PASSAGEIROS, CARGA)")
    public Aeronave criar(@Valid @RequestBody CriarAeronaveRequest req) {
        Aeronave aeronave;
        if (req instanceof CriarAeronaveParticularRequest r) {
            AeronaveParticular a = new AeronaveParticular();
            a.setEmpresaManutencao(r.getEmpresaManutencao());
            aeronave = a;
        } else if (req instanceof CriarAeronavePassageirosRequest r) {
            AeronavePassageiros a = new AeronavePassageiros();
            a.setCompanhiaAerea(r.getCompanhiaAerea());
            a.setCapacidadePassageiros(r.getCapacidadePassageiros());
            aeronave = a;
        } else if (req instanceof CriarAeronaveCargaRequest r) {
            AeronaveCarga a = new AeronaveCarga();
            a.setCompanhiaAerea(r.getCompanhiaAerea());
            a.setCapacidadeCargaToneladas(r.getCapacidadeCargaToneladas());
            aeronave = a;
        } else {
            throw new IllegalArgumentException("Tipo de aeronave desconhecido");
        }
        aeronave.setPrefixo(req.getPrefixo().toUpperCase());
        aeronave.setVelocidadeCruzeiro(req.getVelocidadeCruzeiro());
        aeronave.setAutonomia(req.getAutonomia());
        return aeronaveService.criar(aeronave);
    }
}
