package com.br.startup.tolevBack.simulations.application.dto.response;

import java.math.BigDecimal;

public record FutureBalanceSimulationResponse(
    Long idContaBancaria,
    BigDecimal saldoAtual,
    BigDecimal saldoProjetado,
    Integer meses,
    BigDecimal variacaoMensalMedia
) {}
