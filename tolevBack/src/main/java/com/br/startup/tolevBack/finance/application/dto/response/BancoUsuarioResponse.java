package com.br.startup.tolevBack.finance.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Um banco que o usuário de fato usa, visto pelo que já entrou na plataforma.
 *
 * <p>Diferente de {@link BankResponse}, que é o catálogo — a lista de onde
 * escolher na hora de subir o extrato. Aqui só aparece banco com transação
 * importada, e cada linha responde "o que eu já trouxe deste banco?".
 *
 * @param importadoAte     o marco: até que data este banco está coberto. Subir o
 *                         extrato de novo só acrescenta o que for posterior
 * @param ultimaImportacao quando foi o último upload concluído deste banco
 */
public record BancoUsuarioResponse(
    Long idBanco,
    String nome,
    String codigoBanco,
    long quantidadeTransacoes,
    BigDecimal totalEntradas,
    BigDecimal totalSaidas,
    LocalDate primeiraTransacao,
    LocalDate ultimaTransacao,
    LocalDate importadoAte,
    LocalDateTime ultimaImportacao
) {}
