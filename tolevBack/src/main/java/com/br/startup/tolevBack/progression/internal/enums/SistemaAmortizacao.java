package com.br.startup.tolevBack.progression.internal.enums;

/**
 * Como a dívida é amortizada ao longo das parcelas.
 *
 * <ul>
 *   <li>{@link #PRICE} — parcela constante; a amortização cresce e os juros caem.</li>
 *   <li>{@link #SAC} — amortização constante; a parcela começa maior e vai caindo.</li>
 * </ul>
 */
public enum SistemaAmortizacao {
    PRICE,
    SAC
}
