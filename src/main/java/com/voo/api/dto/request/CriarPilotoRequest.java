package com.voo.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CriarPilotoRequest {
    @NotBlank private String registro;
    @NotBlank private String nome;

    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
