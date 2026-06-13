package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    Long idContaBancaria,
    Long idVendedor,
    BigDecimal valor,
    LocalDate dataTransacao,
    TipoTransacao tipo,
    String descricao,
    String descricaoNormalizada,
    Boolean parcelado,
    BigDecimal totalParcelas,
    BigDecimal numeroParcela,
    MetodoPagamento metodoPagamento
) {}
