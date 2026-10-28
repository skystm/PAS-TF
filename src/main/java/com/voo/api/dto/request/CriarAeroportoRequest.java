package com.voo.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CriarAeroportoRequest {
    @NotBlank private String codigo;
    @NotBlank private String nome;
    @Positive private int vagasEstacionamento;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getVagasEstacionamento() { return vagasEstacionamento; }
    public void setVagasEstacionamento(int vagasEstacionamento) { this.vagasEstacionamento = vagasEstacionamento; }
}
