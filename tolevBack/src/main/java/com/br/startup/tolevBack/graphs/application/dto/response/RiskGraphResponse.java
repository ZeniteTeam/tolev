package com.br.startup.tolevBack.graphs.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RiskGraphResponse(
    Long idUsuario,
    String nivelRiscoGeral,
    Integer totalAnalises,
    List<RiskDataPoint> indicadores
) {
    public record RiskDataPoint(String tipo, String resultadoResumo, String relevancia, LocalDateTime dataCriacao) {}
}
