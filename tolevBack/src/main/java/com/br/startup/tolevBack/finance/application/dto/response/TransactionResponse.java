package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma transação como o app a exibe
 * */
public record TransactionResponse(
    Long id,
    Long idUsuario,
    Long idContaBancaria,
    Long idVendedor,
    String nomeVendedor,
    BigDecimal valor,
    LocalDate dataTransacao,
    TipoTransacao tipo,
    String descricao,
    String descricaoNormalizada,
    Boolean parcelado,
    BigDecimal totalParcelas,
    BigDecimal numeroParcela,
    MetodoPagamento metodoPagamento,
    Long idCategoriaGastoSistema,
    Long idCategoriaGastoUsuario,
    String nomeCategoria,
    String corCategoria
) {}
