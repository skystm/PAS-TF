package com.voo.api.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "piloto")
public class Piloto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registro;

    @Column(nullable = false)
    private String nome;

    public Piloto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
