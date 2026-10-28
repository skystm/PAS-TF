package com.voo.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ConsultaDisponibilidadeRequest {
    @NotNull private LocalDateTime inicio;
    @NotNull private LocalDateTime fim;

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
}
