package com.voo.api.dto.request;

public class CriarAeronaveParticularRequest extends CriarAeronaveRequest {
    private String empresaManutencao;

    public String getEmpresaManutencao() { return empresaManutencao; }
    public void setEmpresaManutencao(String empresaManutencao) { this.empresaManutencao = empresaManutencao; }
}
