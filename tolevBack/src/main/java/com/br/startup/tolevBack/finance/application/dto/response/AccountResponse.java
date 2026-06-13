package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.Moeda;
import com.br.startup.tolevBack.finance.internal.enums.StatusConta;
import com.br.startup.tolevBack.finance.internal.enums.TipoConta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(
    Long id,
    Long idUsuario,
    Long idBanco,
    String tituloBanco,
    String numeroConta,
    String nomeConta,
    TipoConta tipoConta,
    Moeda moeda,
    BigDecimal saldoAtual,
    BigDecimal saldoDisponivel,
    BigDecimal limiteCredito,
    StatusConta statusConta,
    LocalDate dataAbertura
) {}
