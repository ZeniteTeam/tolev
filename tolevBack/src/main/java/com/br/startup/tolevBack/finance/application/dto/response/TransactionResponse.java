package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma transação como o app a exibe
 *
 * @param idBanco de qual banco veio o lançamento; preenchido só nas transações
 *                importadas de extrato, onde o usuário escolheu o banco antes de
 *                subir o PDF. É o que permite filtrar a análise por banco
 * */
public record TransactionResponse(
    Long id,
    Long idUsuario,
    Long idContaBancaria,
    Long idBanco,
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
