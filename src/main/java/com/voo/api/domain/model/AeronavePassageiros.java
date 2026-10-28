package com.voo.api.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PASSAGEIROS")
public class AeronavePassageiros extends Aeronave {

    @Column(name = "companhia_aerea")
    private String companhiaAerea;

    @Column(name = "capacidade_passageiros")
    private int capacidadePassageiros;

    public AeronavePassageiros() {}

    public String getCompanhiaAerea() { return companhiaAerea; }
    public void setCompanhiaAerea(String companhiaAerea) { this.companhiaAerea = companhiaAerea; }
    public int getCapacidadePassageiros() { return capacidadePassageiros; }
    public void setCapacidadePassageiros(int capacidadePassageiros) { this.capacidadePassageiros = capacidadePassageiros; }

    @Override
    public boolean altitudePermitida(int altitude) {
        return altitude >= 28000 && altitude <= 35000 && altitude % 1000 == 0;
    }

    @Override
    public boolean horarioPermitido(LocalDateTime inicio, LocalDateTime fim) {
        return true;
    }
}
