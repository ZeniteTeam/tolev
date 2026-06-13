package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;

public record BankResponse(
    Long id,
    String titulo,
    BigDecimal agencia
) {}
