package com.br.startup.tolevBack.users.internal.enums;

/**
 * Método de orçamento escolhido pelo usuário para distribuir a renda mensal.
 */
public enum MetodoOrcamento {
    /** Regra 50/30/20: fixos / quitação de dívidas / lazer (percentuais ajustáveis). */
    REGRA_50_30_20,
    /** Orçamento base zero: cada real da renda recebe uma função. */
    BASE_ZERO,
    /** Envelopes: separa o dinheiro por categoria em envelopes digitais. */
    ENVELOPES
}
