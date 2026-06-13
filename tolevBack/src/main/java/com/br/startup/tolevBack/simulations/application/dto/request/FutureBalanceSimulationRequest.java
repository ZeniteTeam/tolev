package com.br.startup.tolevBack.simulations.application.dto.request;

public record FutureBalanceSimulationRequest(
    Long idUsuario,
    Long idContaBancaria,
    Integer meses
) {}
