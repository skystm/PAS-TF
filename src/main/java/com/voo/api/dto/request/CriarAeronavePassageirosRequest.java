package com.voo.api.dto.request;

public class CriarAeronavePassageirosRequest extends CriarAeronaveRequest {
    private String companhiaAerea;
    private int capacidadePassageiros;

    public String getCompanhiaAerea() { return companhiaAerea; }
    public void setCompanhiaAerea(String companhiaAerea) { this.companhiaAerea = companhiaAerea; }
    public int getCapacidadePassageiros() { return capacidadePassageiros; }
    public void setCapacidadePassageiros(int capacidadePassageiros) { this.capacidadePassageiros = capacidadePassageiros; }
}
