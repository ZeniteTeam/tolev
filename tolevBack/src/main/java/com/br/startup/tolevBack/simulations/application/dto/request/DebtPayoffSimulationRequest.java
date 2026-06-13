package com.br.startup.tolevBack.simulations.application.dto.request;

import java.math.BigDecimal;

public record DebtPayoffSimulationRequest(
    Long idUsuario,
    Long idDivida,
    BigDecimal pagamentoMensal
) {}
