package com.br.startup.tolevBack.users.internal.enums;

/**
 * Estratégia de quitação de dívidas escolhida pelo usuário. Define como as
 * dívidas são priorizadas nas projeções, análises e recomendações.
 */
public enum MetodoQuitacao {
    /** Ataca primeiro a dívida com a maior taxa de juros (menor custo total). */
    AVALANCHE,
    /** Quita primeiro a dívida de menor saldo (vitórias rápidas / motivação). */
    SNOWBALL,
    /** Prioriza a dívida de maior peso emocional (alívio psicológico). */
    TSUNAMI
}
