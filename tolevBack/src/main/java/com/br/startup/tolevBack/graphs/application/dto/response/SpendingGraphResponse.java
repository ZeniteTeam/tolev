package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SpendingGraphResponse(
    Long idUsuario,
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldoLiquido,
    List<SpendingDataPoint> pontos
) {
    public record SpendingDataPoint(String tipo, BigDecimal valor, BigDecimal percentual) {}
}
