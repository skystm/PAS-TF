package com.voo.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_aerovia")
public class ReservaAerovia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aerovia_id", nullable = false)
    private Aerovia aerovia;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    @Column(name = "slot_fim", nullable = false)
    private LocalDateTime slotFim;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_voo_id", nullable = false)
    private PlanoVoo planoVoo;

    public ReservaAerovia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aerovia getAerovia() { return aerovia; }
    public void setAerovia(Aerovia aerovia) { this.aerovia = aerovia; }
    public int getAltitude() { return altitude; }
    public void setAltitude(int altitude) { this.altitude = altitude; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
    public void setSlotInicio(LocalDateTime slotInicio) { this.slotInicio = slotInicio; }
    public LocalDateTime getSlotFim() { return slotFim; }
    public void setSlotFim(LocalDateTime slotFim) { this.slotFim = slotFim; }
    public PlanoVoo getPlanoVoo() { return planoVoo; }
    public void setPlanoVoo(PlanoVoo planoVoo) { this.planoVoo = planoVoo; }
}
