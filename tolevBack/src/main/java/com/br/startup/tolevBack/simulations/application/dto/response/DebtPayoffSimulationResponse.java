package com.br.startup.tolevBack.simulations.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtPayoffSimulationResponse(
    Long idDivida,
    BigDecimal valorTotalDivida,
    BigDecimal valorJaPago,
    BigDecimal valorRestante,
    BigDecimal pagamentoMensal,
    Integer mesesParaQuitar,
    LocalDate dataPrevistaQuitacao
) {}
