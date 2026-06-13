package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;

public record DebtPayoffEstimateResponse(
    Long idUsuario,
    BigDecimal totalDividas,
    BigDecimal totalPago,
    BigDecimal totalRestante,
    BigDecimal percentualPago
) {}
