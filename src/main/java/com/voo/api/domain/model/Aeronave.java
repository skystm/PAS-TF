package com.voo.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "aeronave")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AeronaveParticular.class, name = "PARTICULAR"),
    @JsonSubTypes.Type(value = AeronavePassageiros.class, name = "PASSAGEIROS"),
    @JsonSubTypes.Type(value = AeronaveCarga.class, name = "CARGA")
})
public abstract class Aeronave {

    @Id
    @Column(length = 10)
    private String prefixo;

    @Column(name = "velocidade_cruzeiro", nullable = false)
    private double velocidadeCruzeiro;

    @Column(nullable = false)
    private double autonomia;

    public Aeronave() {}

    public String getPrefixo() { return prefixo; }
    public void setPrefixo(String prefixo) { this.prefixo = prefixo; }
    public double getVelocidadeCruzeiro() { return velocidadeCruzeiro; }
    public void setVelocidadeCruzeiro(double velocidadeCruzeiro) { this.velocidadeCruzeiro = velocidadeCruzeiro; }
    public double getAutonomia() { return autonomia; }
    public void setAutonomia(double autonomia) { this.autonomia = autonomia; }

    public abstract boolean altitudePermitida(int altitude);
    public abstract boolean horarioPermitido(java.time.LocalDateTime inicio, java.time.LocalDateTime fim);

    public double autonomiaEfetiva() {
        return autonomia * 0.85;
    }
}
