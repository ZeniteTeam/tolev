package com.br.startup.tolevBack.analysis.application.dto.response;

import java.math.BigDecimal;

/**
 * Uma variável que entrou no cálculo, com o quanto ela puxou o resultado.
 *
 * @param valorFaixa      nota 0–100 dessa variável isolada
 * @param peso            quanto ela vale no composto
 * @param coeficiente     contribuição efetiva (peso × valorFaixa) — o que o gráfico desenha
 * @param faixaReferencia o alvo, em texto
 */
public record AnalysisVariableResponse(
    String nome,
    String valor,
    BigDecimal valorFaixa,
    BigDecimal peso,
    BigDecimal coeficiente,
    String impactoResultado,
    String faixaReferencia
) {}
