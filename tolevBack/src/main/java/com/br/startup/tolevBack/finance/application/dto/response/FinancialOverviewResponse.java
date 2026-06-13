package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;

public record FinancialOverviewResponse(
    Long idUsuario,
    BigDecimal totalSaldo,
    BigDecimal totalSaldoDisponivel,
    BigDecimal totalLimiteCredito,
    BigDecimal mediaReceita,
    BigDecimal mediaDespesa,
    Integer totalContas
) {}
