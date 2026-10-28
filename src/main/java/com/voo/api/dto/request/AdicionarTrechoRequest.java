package com.voo.api.dto.request;

import com.voo.api.domain.model.TipoPassagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AdicionarTrechoRequest {
    @NotBlank private String aeroviaId;
    @NotNull private Integer altitude;
    @NotNull private LocalDateTime horaEntrada;
    @NotNull private TipoPassagem tipoPassagem;
    private Integer numeroVaga;
    private Integer tempoPermanencia;

    public String getAeroviaId() { return aeroviaId; }
    public void setAeroviaId(String aeroviaId) { this.aeroviaId = aeroviaId; }
    public Integer getAltitude() { return altitude; }
    public void setAltitude(Integer altitude) { this.altitude = altitude; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public TipoPassagem getTipoPassagem() { return tipoPassagem; }
    public void setTipoPassagem(TipoPassagem tipoPassagem) { this.tipoPassagem = tipoPassagem; }
    public Integer getNumeroVaga() { return numeroVaga; }
    public void setNumeroVaga(Integer numeroVaga) { this.numeroVaga = numeroVaga; }
    public Integer getTempoPermanencia() { return tempoPermanencia; }
    public void setTempoPermanencia(Integer tempoPermanencia) { this.tempoPermanencia = tempoPermanencia; }
}
