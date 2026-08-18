package com.br.startup.tolevBack.analysis.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Aritmética que os analisadores repetem o tempo todo.
 *
 * <p>Existe por dois motivos práticos: divisão por zero é o caso comum aqui
 * (usuário sem renda declarada, sem despesa no período, sem dívida) e
 * {@link BigDecimal#divide} explode nesses casos; e nota de faixa aparece em
 * toda variável de todo analisador.
 */
public final class Calculo {

    private Calculo() {
    }

    public static final BigDecimal CEM = new BigDecimal("100");
    public static final BigDecimal DOZE = new BigDecimal("12");

    /** Null vira zero. Quase todo campo financeiro do banco é anulável. */
    public static BigDecimal nz(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    /** Divisão que devolve zero em vez de estourar quando o divisor é zero/nulo. */
    public static BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor) {
        BigDecimal d = nz(divisor);
        if (d.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return nz(dividendo).divide(d, 6, RoundingMode.HALF_UP);
    }

    /** Quanto {@code parte} representa de {@code total}, em pontos percentuais. */
    public static BigDecimal percentual(BigDecimal parte, BigDecimal total) {
        return dividir(parte, total).multiply(CEM).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal dinheiro(BigDecimal valor) {
        return nz(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal nota(BigDecimal valor) {
        return nz(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal limitar(BigDecimal valor, BigDecimal minimo, BigDecimal maximo) {
        return nz(valor).max(minimo).min(maximo);
    }

    /**
     * Nota 0–100 por faixas, para indicador em que <em>menos é melhor</em>
     * (comprometimento de renda, despesa sobre receita).
     *
     * <p>Interpola dentro da faixa em vez de dar degraus: 31% e 39% de
     * comprometimento não podem valer a mesma nota, senão o gráfico de evolução
     * fica plano enquanto a situação piora.
     *
     * @param limites  fronteiras crescentes das faixas
     * @param notas    nota em cada fronteira; mesmo tamanho de {@code limites}
     */
    public static BigDecimal notaDecrescente(BigDecimal valor, double[] limites, double[] notas) {
        double v = nz(valor).doubleValue();
        if (v <= limites[0]) {
            return BigDecimal.valueOf(notas[0]).setScale(2, RoundingMode.HALF_UP);
        }
        for (int i = 0; i < limites.length - 1; i++) {
            if (v <= limites[i + 1]) {
                double fracao = (v - limites[i]) / (limites[i + 1] - limites[i]);
                double nota = notas[i] - fracao * (notas[i] - notas[i + 1]);
                return BigDecimal.valueOf(nota).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(notas[notas.length - 1]).setScale(2, RoundingMode.HALF_UP);
    }

    /** Igual a {@link #notaDecrescente}, para indicador em que <em>mais é melhor</em>. */
    public static BigDecimal notaCrescente(BigDecimal valor, double[] limites, double[] notas) {
        double v = nz(valor).doubleValue();
        if (v <= limites[0]) {
            return BigDecimal.valueOf(notas[0]).setScale(2, RoundingMode.HALF_UP);
        }
        for (int i = 0; i < limites.length - 1; i++) {
            if (v <= limites[i + 1]) {
                double fracao = (v - limites[i]) / (limites[i + 1] - limites[i]);
                double nota = notas[i] + fracao * (notas[i + 1] - notas[i]);
                return BigDecimal.valueOf(nota).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(notas[notas.length - 1]).setScale(2, RoundingMode.HALF_UP);
    }

    /** Logística padrão, usada para transformar o score de risco em probabilidade. */
    public static BigDecimal logistica(BigDecimal z) {
        double p = 1.0 / (1.0 + Math.exp(-nz(z).doubleValue()));
        return BigDecimal.valueOf(p).setScale(4, RoundingMode.HALF_UP);
    }

    /** Formata para o texto que o usuário lê: 1234.5 → "1.234,50". */
    public static String formatarMoeda(BigDecimal valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.of("pt", "BR"));
        return new DecimalFormat("#,##0.00", simbolos).format(nz(valor));
    }

    /** Formata percentual sem casas desnecessárias: 30.00 → "30", 30.50 → "30,5". */
    public static String formatarPercentual(BigDecimal valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.of("pt", "BR"));
        return new DecimalFormat("#,##0.#", simbolos).format(nz(valor));
    }
}
