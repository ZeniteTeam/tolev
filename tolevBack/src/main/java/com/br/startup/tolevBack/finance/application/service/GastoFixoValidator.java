package com.br.startup.tolevBack.finance.application.service;

import java.math.BigDecimal;

/**
 * As regras que um gasto fixo precisa respeitar para servir de insumo de
 * cálculo. São poucas de propósito: o cadastro vale pela rapidez, e recusar
 * entrada por capricho é o que faz a pessoa desistir no terceiro gasto.
 */
public final class GastoFixoValidator {

    private static final int MAX_NOME = 120;

    private GastoFixoValidator() {}

    public static String nomeValido(String nome) {
        String limpo = nome == null ? "" : nome.trim();
        if (limpo.isEmpty()) {
            throw new IllegalArgumentException("Dê um nome ao gasto fixo.");
        }
        if (limpo.length() > MAX_NOME) {
            throw new IllegalArgumentException("O nome do gasto fixo é longo demais.");
        }
        return limpo;
    }

    /**
     * Zero é recusado junto dos negativos: um gasto fixo de R$ 0 não muda
     * conta nenhuma e só ocupa linha na lista que a pessoa precisa conferir
     * todo mês.
     */
    public static BigDecimal valorValido(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("Informe quanto este gasto custa por mês.");
        }
        return valor;
    }

    /** Nulo passa: nem todo gasto fixo tem dia certo, e inventar um seria pior. */
    public static Integer diaValido(Integer dia) {
        if (dia == null) return null;
        if (dia < 1 || dia > 31) {
            throw new IllegalArgumentException("O dia do vencimento vai de 1 a 31.");
        }
        return dia;
    }
}
