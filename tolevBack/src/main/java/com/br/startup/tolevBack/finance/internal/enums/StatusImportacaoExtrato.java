package com.br.startup.tolevBack.finance.internal.enums;

/**
 * Em que pé está um upload de extrato.
 *
 * <p>Existe porque a leitura do PDF saiu da thread da requisição: sem um estado
 * persistido, o app não teria como responder "e aí, terminou?" depois de ter
 * sido fechado e reaberto.
 */
public enum StatusImportacaoExtrato {

    /** O Gemini está lendo o PDF. Nenhuma transação foi gravada ainda. */
    PROCESSANDO,

    /** Os lançamentos viraram transações; o resultado está preenchido. */
    CONCLUIDA,

    /**
     * Nada foi gravado e o motivo está em {@code erro}.
     *
     * <p>Também cobre o caso "o extrato não trouxe nada aproveitável", que não é
     * defeito técnico mas termina igual para o usuário: nenhuma transação nova e
     * uma frase explicando o porquê.
     */
    FALHOU
}
