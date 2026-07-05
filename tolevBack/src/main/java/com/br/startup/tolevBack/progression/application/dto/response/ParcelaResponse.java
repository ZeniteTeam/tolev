package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaResponse(
    Long id,
    Integer numeroParcela,
    BigDecimal valorTotal,
    StatusParcela status,
    LocalDate dataVencimento,
    LocalDate dataPagamento
) {}
