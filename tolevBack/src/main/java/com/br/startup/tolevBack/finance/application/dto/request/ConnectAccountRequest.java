package com.br.startup.tolevBack.finance.application.dto.request;

import com.br.startup.tolevBack.finance.internal.enums.Moeda;
import com.br.startup.tolevBack.finance.internal.enums.TipoConta;

import java.math.BigDecimal;

public record ConnectAccountRequest(
    Long idUsuario,
    Long idBanco,
    String numeroConta,
    TipoConta tipoConta,
    String nomeConta,
    Moeda moeda,
    Boolean contaConjunta,
    BigDecimal limiteCredito,
    BigDecimal agencia
) {}
