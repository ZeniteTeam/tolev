package com.br.startup.tolevBack.progression.internal.enums;

/**
 * Regime de incidência dos juros da dívida.
 *
 * <ul>
 *   <li>{@link #SIMPLES} — os juros incidem sempre sobre o valor original contratado.</li>
 *   <li>{@link #COMPOSTO} — os juros incidem sobre o saldo devedor de cada período.</li>
 * </ul>
 */
public enum RegimeJuros {
    SIMPLES,
    COMPOSTO
}
