package com.voo.api.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CriarAeronaveParticularRequest.class, name = "PARTICULAR"),
    @JsonSubTypes.Type(value = CriarAeronavePassageirosRequest.class, name = "PASSAGEIROS"),
    @JsonSubTypes.Type(value = CriarAeronaveCargaRequest.class, name = "CARGA")
})
public abstract class CriarAeronaveRequest {
    @NotBlank private String prefixo;
    @Positive private double velocidadeCruzeiro;
    @Positive private double autonomia;

    public String getPrefixo() { return prefixo; }
    public void setPrefixo(String prefixo) { this.prefixo = prefixo; }
    public double getVelocidadeCruzeiro() { return velocidadeCruzeiro; }
    public void setVelocidadeCruzeiro(double velocidadeCruzeiro) { this.velocidadeCruzeiro = velocidadeCruzeiro; }
    public double getAutonomia() { return autonomia; }
    public void setAutonomia(double autonomia) { this.autonomia = autonomia; }
}
