package com.voo.api.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "aeroporto")
public class Aeroporto {

    @Id
    @Column(length = 10)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(name = "vagas_estacionamento", nullable = false)
    private int vagasEstacionamento;

    public Aeroporto() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getVagasEstacionamento() { return vagasEstacionamento; }
    public void setVagasEstacionamento(int vagasEstacionamento) { this.vagasEstacionamento = vagasEstacionamento; }
}
