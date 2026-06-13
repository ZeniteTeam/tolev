package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtProjectionResponse(
    Long idDivida,
    BigDecimal valorDivida,
    StatusDivida status,
    BigDecimal progresso,
    BigDecimal peso,
    LocalDate ultimoProgresso
) {}
