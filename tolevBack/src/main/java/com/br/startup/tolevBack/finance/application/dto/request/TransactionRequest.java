package com.br.startup.tolevBack.finance.application.dto.request;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
    Long idContaBancaria,
    Long idVendedor,
    BigDecimal valor,
    LocalDate dataTransacao,
    TipoTransacao tipo,
    String descricao,
    Boolean parcelado,
    BigDecimal totalParcelas,
    BigDecimal numeroParcela,
    MetodoPagamento metodoPagamento
) {}
