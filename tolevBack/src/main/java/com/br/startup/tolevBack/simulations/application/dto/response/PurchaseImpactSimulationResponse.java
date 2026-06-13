package com.br.startup.tolevBack.simulations.application.dto.response;

import java.math.BigDecimal;

public record PurchaseImpactSimulationResponse(
    Long idContaBancaria,
    BigDecimal saldoAtual,
    BigDecimal saldoDisponivel,
    BigDecimal valorCompra,
    BigDecimal saldoAposCompra,
    BigDecimal saldoDisponivelAposCompra,
    Boolean parcelado,
    Integer numeroParcelas,
    BigDecimal impactoMensalParcela,
    Boolean suficiente
) {}
