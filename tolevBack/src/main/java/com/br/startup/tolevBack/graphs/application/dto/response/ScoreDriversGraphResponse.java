package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * O que está sustentando (ou derrubando) a nota atual.
 *
 * <p>Feito para gráfico de barras: cada driver traz a contribuição que teve no
 * score, o quanto ainda pode render e onde ele deveria estar.
 *
 * @param pontosPerdidos quanto o usuário deixou na mesa somando todos os drivers
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
        /** peso × nota: o que ela efetivamente somou ao score. */
        BigDecimal contribuicao,
        /** peso × 100: o máximo que ela poderia somar. */
        BigDecimal contribuicaoMaxima,
        String impacto,
        String faixaReferencia
    ) {}
}
