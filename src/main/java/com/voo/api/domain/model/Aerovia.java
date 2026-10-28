package com.voo.api.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "aerovia")
public class Aerovia {

    @Id
    @Column(length = 10)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origem_codigo", nullable = false)
    private Aeroporto origem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destino_codigo", nullable = false)
    private Aeroporto destino;

    @Column(nullable = false)
    private double distancia;

    public Aerovia() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Aeroporto getOrigem() { return origem; }
    public void setOrigem(Aeroporto origem) { this.origem = origem; }
    public Aeroporto getDestino() { return destino; }
    public void setDestino(Aeroporto destino) { this.destino = destino; }
    public double getDistancia() { return distancia; }
    public void setDistancia(double distancia) { this.distancia = distancia; }
}
