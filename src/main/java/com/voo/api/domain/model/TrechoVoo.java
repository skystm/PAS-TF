package com.voo.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trecho_voo")
public class TrechoVoo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_voo_id", nullable = false)
    private PlanoVoo planoVoo;

    @Column(nullable = false)
    private int ordem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aerovia_id", nullable = false)
    private Aerovia aerovia;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "hora_entrada", nullable = false)
    private LocalDateTime horaEntrada;

    @Column(name = "hora_saida", nullable = false)
    private LocalDateTime horaSaida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_passagem", nullable = false)
    private TipoPassagem tipoPassagem;

    @Column(name = "numero_vaga")
    private Integer numeroVaga;

    @Column(name = "tempo_permanencia")
    private Integer tempoPermanencia;

    public TrechoVoo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlanoVoo getPlanoVoo() { return planoVoo; }
    public void setPlanoVoo(PlanoVoo planoVoo) { this.planoVoo = planoVoo; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
    public Aerovia getAerovia() { return aerovia; }
    public void setAerovia(Aerovia aerovia) { this.aerovia = aerovia; }
    public int getAltitude() { return altitude; }
    public void setAltitude(int altitude) { this.altitude = altitude; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public LocalDateTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalDateTime horaSaida) { this.horaSaida = horaSaida; }
    public TipoPassagem getTipoPassagem() { return tipoPassagem; }
    public void setTipoPassagem(TipoPassagem tipoPassagem) { this.tipoPassagem = tipoPassagem; }
    public Integer getNumeroVaga() { return numeroVaga; }
    public void setNumeroVaga(Integer numeroVaga) { this.numeroVaga = numeroVaga; }
    public Integer getTempoPermanencia() { return tempoPermanencia; }
    public void setTempoPermanencia(Integer tempoPermanencia) { this.tempoPermanencia = tempoPermanencia; }

    public LocalDateTime horaChegada() { return horaSaida; }

    public LocalDateTime horaPartidaProximoTrecho() {
        if (tipoPassagem == TipoPassagem.POUSO && tempoPermanencia != null) {
            return horaSaida.plusMinutes(tempoPermanencia);
        }
        return horaSaida;
    }
}
