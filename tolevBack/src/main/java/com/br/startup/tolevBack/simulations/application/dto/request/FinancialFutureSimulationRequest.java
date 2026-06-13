package com.br.startup.tolevBack.simulations.application.dto.request;

public record FinancialFutureSimulationRequest(
    Long idUsuario,
    Integer meses,
    String cenario
) {}
