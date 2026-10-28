package com.voo.api.dto.request;

public class CriarAeronaveCargaRequest extends CriarAeronaveRequest {
    private String companhiaAerea;
    private double capacidadeCargaToneladas;

    public String getCompanhiaAerea() { return companhiaAerea; }
    public void setCompanhiaAerea(String companhiaAerea) { this.companhiaAerea = companhiaAerea; }
    public double getCapacidadeCargaToneladas() { return capacidadeCargaToneladas; }
    public void setCapacidadeCargaToneladas(double capacidadeCargaToneladas) { this.capacidadeCargaToneladas = capacidadeCargaToneladas; }
}
