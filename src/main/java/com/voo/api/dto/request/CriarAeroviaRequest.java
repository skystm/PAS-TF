package com.voo.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CriarAeroviaRequest {
    @NotBlank private String id;
    @NotBlank private String origemCodigo;
    @NotBlank private String destinoCodigo;
    @Positive private double distancia;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrigemCodigo() { return origemCodigo; }
    public void setOrigemCodigo(String origemCodigo) { this.origemCodigo = origemCodigo; }
    public String getDestinoCodigo() { return destinoCodigo; }
    public void setDestinoCodigo(String destinoCodigo) { this.destinoCodigo = destinoCodigo; }
    public double getDistancia() { return distancia; }
    public void setDistancia(double distancia) { this.distancia = distancia; }
}
