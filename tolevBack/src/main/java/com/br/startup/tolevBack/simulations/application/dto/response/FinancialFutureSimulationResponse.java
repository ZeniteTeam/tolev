package com.br.startup.tolevBack.simulations.application.dto.response;

import java.math.BigDecimal;

public record FinancialFutureSimulationResponse(
    Long idUsuario,
    String cenario,
    Integer meses,
    BigDecimal saldoAtual,
    BigDecimal saldoProjetado,
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal balanceMensalLiquido
) {}
