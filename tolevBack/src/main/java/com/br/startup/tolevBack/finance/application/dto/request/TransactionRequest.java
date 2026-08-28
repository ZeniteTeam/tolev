package com.br.startup.tolevBack.finance.application.dto.request;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * POST /transactions.
 *
 * <p>{@code idContaBancaria} é opcional: um lançamento manual em dinheiro não
 * sai de conta nenhuma. {@code nomeVendedor} é o texto livre do
 * estabelecimento — resolvemos ou criamos o {@code Vendedor} a partir dele, o
 * app não precisa conhecer ids de vendedor.
 *
 * <p>A categoria vem em uma das duas colunas conforme a origem: catálogo do
 * sistema ou categoria criada pelo usuário. Só uma delas pode vir preenchida.
 */
public record TransactionRequest(
    Long idUsuario,
    Long idContaBancaria,
    String nomeVendedor,
    BigDecimal valor,
    LocalDate dataTransacao,
    TipoTransacao tipo,
    String descricao,
    Boolean parcelado,
    BigDecimal totalParcelas,
    BigDecimal numeroParcela,
    MetodoPagamento metodoPagamento,
    Long idCategoriaGastoSistema,
    Long idCategoriaGastoUsuario
) {}
