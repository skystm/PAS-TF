package com.voo.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_vaga")
public class ReservaVaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aeroporto_codigo", nullable = false)
    private Aeroporto aeroporto;

    @Column(name = "numero_vaga", nullable = false)
    private int numeroVaga;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    @Column(name = "slot_fim", nullable = false)
    private LocalDateTime slotFim;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_voo_id", nullable = false)
    private PlanoVoo planoVoo;

    public ReservaVaga() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aeroporto getAeroporto() { return aeroporto; }
    public void setAeroporto(Aeroporto aeroporto) { this.aeroporto = aeroporto; }
    public int getNumeroVaga() { return numeroVaga; }
    public void setNumeroVaga(int numeroVaga) { this.numeroVaga = numeroVaga; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
    public void setSlotInicio(LocalDateTime slotInicio) { this.slotInicio = slotInicio; }
    public LocalDateTime getSlotFim() { return slotFim; }
    public void setSlotFim(LocalDateTime slotFim) { this.slotFim = slotFim; }
    public PlanoVoo getPlanoVoo() { return planoVoo; }
    public void setPlanoVoo(PlanoVoo planoVoo) { this.planoVoo = planoVoo; }
}
