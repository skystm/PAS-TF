package com.voo.api.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("CARGA")
public class AeronaveCarga extends Aeronave {

    @Column(name = "companhia_aerea")
    private String companhiaAerea;

    @Column(name = "capacidade_carga_toneladas")
    private double capacidadeCargaToneladas;

    public AeronaveCarga() {}

    public String getCompanhiaAerea() { return companhiaAerea; }
    public void setCompanhiaAerea(String companhiaAerea) { this.companhiaAerea = companhiaAerea; }
    public double getCapacidadeCargaToneladas() { return capacidadeCargaToneladas; }
    public void setCapacidadeCargaToneladas(double capacidadeCargaToneladas) { this.capacidadeCargaToneladas = capacidadeCargaToneladas; }

    @Override
    public boolean altitudePermitida(int altitude) {
        return altitude >= 25000 && altitude <= 35000 && altitude % 1000 == 0;
    }

    // All airway usage must be between 00:00 (inclusive) and 06:00 (exclusive)
    @Override
    public boolean horarioPermitido(LocalDateTime inicio, LocalDateTime fim) {
        if (!inicio.toLocalDate().equals(fim.toLocalDate())) {
            return false;
        }
        LocalTime endTime = fim.toLocalTime();
        LocalTime limite = LocalTime.of(6, 0);
        return endTime.isBefore(limite) || endTime.equals(LocalTime.MIDNIGHT);
    }
}
