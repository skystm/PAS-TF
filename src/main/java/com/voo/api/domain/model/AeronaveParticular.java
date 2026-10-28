package com.voo.api.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PARTICULAR")
public class AeronaveParticular extends Aeronave {

    @Column(name = "empresa_manutencao")
    private String empresaManutencao;

    public AeronaveParticular() {}

    public String getEmpresaManutencao() { return empresaManutencao; }
    public void setEmpresaManutencao(String empresaManutencao) { this.empresaManutencao = empresaManutencao; }

    @Override
    public boolean altitudePermitida(int altitude) {
        return altitude == 25000 || altitude == 26000 || altitude == 27000;
    }

    @Override
    public boolean horarioPermitido(LocalDateTime inicio, LocalDateTime fim) {
        return true;
    }
}
