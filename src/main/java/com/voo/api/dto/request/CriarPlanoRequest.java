package com.voo.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CriarPlanoRequest {
    @NotNull private Long pilotoId;
    @NotBlank private String aeronavePrefixo;
    @NotBlank private String origemCodigo;
    @NotBlank private String destinoCodigo;
    @NotNull private LocalDateTime dataHoraPartida;

    public Long getPilotoId() { return pilotoId; }
    public void setPilotoId(Long pilotoId) { this.pilotoId = pilotoId; }
    public String getAeronavePrefixo() { return aeronavePrefixo; }
    public void setAeronavePrefixo(String aeronavePrefixo) { this.aeronavePrefixo = aeronavePrefixo; }
    public String getOrigemCodigo() { return origemCodigo; }
    public void setOrigemCodigo(String origemCodigo) { this.origemCodigo = origemCodigo; }
    public String getDestinoCodigo() { return destinoCodigo; }
    public void setDestinoCodigo(String destinoCodigo) { this.destinoCodigo = destinoCodigo; }
    public LocalDateTime getDataHoraPartida() { return dataHoraPartida; }
    public void setDataHoraPartida(LocalDateTime dataHoraPartida) { this.dataHoraPartida = dataHoraPartida; }
}
