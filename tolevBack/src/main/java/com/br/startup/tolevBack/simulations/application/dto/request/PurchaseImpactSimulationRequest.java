package com.br.startup.tolevBack.simulations.application.dto.request;

import java.math.BigDecimal;

public record PurchaseImpactSimulationRequest(
    Long idUsuario,
    Long idContaBancaria,
    BigDecimal valorCompra,
    Boolean parcelado,
    Integer numeroParcelas
) {}
