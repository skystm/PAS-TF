package com.voo.api.controller;

import com.voo.api.domain.model.PlanoVoo;
import com.voo.api.domain.model.SituacaoPlano;
import com.voo.api.dto.request.AdicionarTrechoRequest;
import com.voo.api.dto.request.CriarPlanoRequest;
import com.voo.api.service.PlanoVooService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planos")
@Tag(name = "Planos de Voo", description = "Criação, edição, validação e aprovação de planos de voo")
public class PlanoVooController {

    private final PlanoVooService planoVooService;

    public PlanoVooController(PlanoVooService planoVooService) {
        this.planoVooService = planoVooService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo plano de voo (nasce em edição)")
    public PlanoVoo criar(@Valid @RequestBody CriarPlanoRequest req) {
        return planoVooService.criar(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar plano de voo por ID")
    public PlanoVoo buscar(@PathVariable Long id) {
        return planoVooService.buscar(id);
    }

    @GetMapping
    @Operation(summary = "Listar planos por situação (EM_EDICAO, APROVADO, CANCELADO, ABANDONADO) — omitir para listar todos")
    public List<PlanoVoo> listar(@RequestParam(required = false) SituacaoPlano situacao) {
        return planoVooService.listarPorSituacao(situacao);
    }

    @PostMapping("/{id}/trechos")
    @Operation(summary = "Adicionar trecho ao percurso do plano (deve estar em edição)")
    public PlanoVoo adicionarTrecho(@PathVariable Long id, @Valid @RequestBody AdicionarTrechoRequest req) {
        return planoVooService.adicionarTrecho(id, req);
    }

    @DeleteMapping("/{id}/trechos")
    @Operation(summary = "Remover todos os trechos do plano (deve estar em edição)")
    public PlanoVoo removerTrechos(@PathVariable Long id) {
        return planoVooService.removerTrechos(id);
    }

    @GetMapping("/{id}/validar")
    @Operation(summary = "Validar plano de voo sem alterar seu estado nem reservar recursos")
    public Map<String, Object> validar(@PathVariable Long id) {
        List<String> erros = planoVooService.validar(id);
        if (erros.isEmpty()) {
            return Map.of("valido", true, "erros", List.of());
        }
        return Map.of("valido", false, "erros", erros);
    }

    @PostMapping("/{id}/aprovar")
    @Operation(summary = "Aprovar plano de voo e efetivar reservas (operação atômica)")
    public PlanoVoo aprovar(@PathVariable Long id) {
        return planoVooService.aprovar(id);
    }

    @PostMapping("/{id}/abandonar")
    @Operation(summary = "Abandonar plano em edição")
    public PlanoVoo abandonar(@PathVariable Long id) {
        return planoVooService.abandonar(id);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar plano aprovado (somente se a partida ainda não ocorreu), liberando reservas")
    public PlanoVoo cancelar(@PathVariable Long id) {
        return planoVooService.cancelar(id);
    }
}
