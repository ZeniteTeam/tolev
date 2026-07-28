package com.br.startup.tolevBack.users.internal.enums;

/**
 * Retrato da situação financeira atual declarada pelo usuário no onboarding.
 * Usada para calibrar análises, risco e o tom das recomendações.
 */
public enum SituacaoFinanceira {
    /** As dívidas estão maiores do que a renda consegue sustentar. */
    ENDIVIDADO,
    /** Fecha o mês no zero a zero — ganha e gasta tudo. */
    NO_LIMITE,
    /** Consegue poupar um pouco todo mês. */
    EQUILIBRADO,
    /** Sobra dinheiro e já investe. */
    INVESTINDO
}
