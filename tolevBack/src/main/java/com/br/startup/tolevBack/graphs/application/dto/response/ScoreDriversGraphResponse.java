package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * O que está sustentando (ou derrubando) a nota atual.
 *
 * <p>Feito para gráfico de barras: cada driver traz a contribuição que teve no
 * score, o quanto ainda pode render e onde ele deveria estar.
 *
 * @param scoreTotal     soma das contribuições, na unidade de {@link Driver#escala}.
 *                       Em RISCO vem renormalizado por Σ peso, como no consolidador.
 * @param pontosPerdidos quanto o usuário deixou na mesa somando todos os drivers.
 *                       {@code null} quando a escala não é PONTOS — em logito ou
 *                       reais não existe "máximo" para descontar.
 */
public record ScoreDriversGraphResponse(
    Long idUsuario,
    String tipo,
    BigDecimal scoreTotal,
    BigDecimal pontosPerdidos,
    List<Driver> drivers
) {
    public record Driver(
        String nome,
        String valorAtual,
        /** Nota isolada dessa variável, 0–100. */
        BigDecimal nota,
        BigDecimal peso,
        /** O que a variável efetivamente somou, na unidade de {@link #escala}. */
        BigDecimal contribuicao,
        /** peso × 100: o máximo que ela poderia somar. {@code null} fora de PONTOS. */
        BigDecimal contribuicaoMaxima,
        /**
         * Unidade de {@code contribuicao}: PONTOS, LOGITO, REAIS ou MESES.
         *
         * <p>Só em PONTOS a contribuição é uma fatia de uma nota 0–100 e pode
         * virar barra proporcional. Existe para o app não precisar saber quais
         * tipos de análise aceitam esse desenho — isso é regra de negócio.
         */
        String escala,
        String impacto,
        String faixaReferencia
    ) {}
}
