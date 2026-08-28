package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Linha do tempo da nota de uma análise.
 *
 * @param variacao diferença entre o primeiro e o último ponto — positiva é melhora
 * @param tendencia MELHORANDO, PIORANDO ou ESTAVEL
 */
public record ScoreEvolutionGraphResponse(
    Long idUsuario,
    String tipo,
    BigDecimal scoreAtual,
    BigDecimal scoreAnterior,
    BigDecimal variacao,
    String tendencia,
    String classificacaoAtual,
    List<ScorePoint> pontos
) {
    public record ScorePoint(
        LocalDate data,
        BigDecimal score,
        String classificacao,
        String nivelRisco
    ) {}
}
