package com.voo.api.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plano_voo")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlanoVoo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "piloto_id", nullable = false)
    private Piloto piloto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aeronave_prefixo", nullable = false)
    private Aeronave aeronave;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origem_codigo", nullable = false)
    private Aeroporto origem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destino_codigo", nullable = false)
    private Aeroporto destino;

    @Column(name = "data_hora_partida", nullable = false)
    private LocalDateTime dataHoraPartida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoPlano situacao = SituacaoPlano.EM_EDICAO;

    @OneToMany(mappedBy = "planoVoo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("ordem ASC")
    private List<TrechoVoo> trechos = new ArrayList<>();

    public PlanoVoo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Piloto getPiloto() { return piloto; }
    public void setPiloto(Piloto piloto) { this.piloto = piloto; }
    public Aeronave getAeronave() { return aeronave; }
    public void setAeronave(Aeronave aeronave) { this.aeronave = aeronave; }
    public Aeroporto getOrigem() { return origem; }
    public void setOrigem(Aeroporto origem) { this.origem = origem; }
    public Aeroporto getDestino() { return destino; }
    public void setDestino(Aeroporto destino) { this.destino = destino; }
    public LocalDateTime getDataHoraPartida() { return dataHoraPartida; }
    public void setDataHoraPartida(LocalDateTime dataHoraPartida) { this.dataHoraPartida = dataHoraPartida; }
    public SituacaoPlano getSituacao() { return situacao; }
    public void setSituacao(SituacaoPlano situacao) { this.situacao = situacao; }
    public List<TrechoVoo> getTrechos() { return trechos; }
    public void setTrechos(List<TrechoVoo> trechos) { this.trechos = trechos; }

    public boolean podeSerModificado() { return situacao == SituacaoPlano.EM_EDICAO; }
    public boolean podeSerAprovado() { return situacao == SituacaoPlano.EM_EDICAO; }
    public boolean podeSerAbandonado() { return situacao == SituacaoPlano.EM_EDICAO; }
    public boolean podeSerCancelado(LocalDateTime agora) {
        return situacao == SituacaoPlano.APROVADO && dataHoraPartida.isAfter(agora);
    }
}
