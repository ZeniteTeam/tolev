package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DebtEvolutionGraphResponse(
    Long idUsuario,
    BigDecimal totalDividas,
    BigDecimal totalPago,
    BigDecimal totalRestante,
    BigDecimal percentualPago,
    List<DebtDataPoint> pontos
) {
    public record DebtDataPoint(Long idDivida, BigDecimal valorDivida, BigDecimal progresso, String status) {}
}
