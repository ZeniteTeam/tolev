package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceResponse(
    Long id,
    BigDecimal saldoAtual,
    BigDecimal saldoDisponivel,
    BigDecimal limiteCredito,
    LocalDateTime ultimaAtualizacao
) {}
