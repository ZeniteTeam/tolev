package com.br.startup.tolevBack.finance.application.dto.request;

import java.math.BigDecimal;

/**
 * Um gasto fixo declarado pelo usuário.
 *
 * <p>Três campos de propósito. O cadastro precisa ser rápido o bastante para a
 * pessoa registrar aluguel, internet e academia numa sentada — cada campo a
 * mais é uma chance de ela parar no meio e o app ficar sem gasto fixo nenhum,
 * que é o estado em que ele não serve para nada.
 *
 * @param idUsuario      dono; obrigatório na criação, ignorado na edição
 * @param nome           como a pessoa chama ("Aluguel", "Internet")
 * @param valor          quanto sai por mês
 * @param diaVencimento  1–31, opcional: nem todo gasto tem dia fixo
 */
public record GastoFixoRequest(
    Long idUsuario,
    String nome,
    BigDecimal valor,
    Integer diaVencimento
) {}
