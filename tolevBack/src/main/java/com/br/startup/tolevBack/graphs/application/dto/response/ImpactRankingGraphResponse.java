package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ranking do que custa dinheiro ao usuário, em reais por ano.
 *
 * <p>É a leitura mais direta que a tabela de análise permite: em vez de "sua
 * nota é 62", mostra "delivery te custa R$ 2.784 por ano".
 */
public record ImpactRankingGraphResponse(
    Long idUsuario,
    BigDecimal custoAnualTotal,
    BigDecimal custoMensalTotal,
    List<ImpactoPonto> pontos
) {
    public record ImpactoPonto(
        String regra,
        String descricao,
        String entidadeTipo,
        Long entidadeId,
        String gravidade,
        BigDecimal custoMensal,
        BigDecimal custoAnual,
        /** Fatia desse achado no custo anual total, em %. */
        BigDecimal percentualDoTotal
    ) {}
}
