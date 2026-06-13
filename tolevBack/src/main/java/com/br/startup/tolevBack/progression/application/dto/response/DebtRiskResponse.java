package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;

public record DebtRiskResponse(
    Long idUsuario,
    String nivelRisco,
    Integer totalDividas,
    Integer dividasAtivas,
    Integer dividasAtrasadas,
    Integer dividasPagas,
    BigDecimal valorTotalDividas
) {}
