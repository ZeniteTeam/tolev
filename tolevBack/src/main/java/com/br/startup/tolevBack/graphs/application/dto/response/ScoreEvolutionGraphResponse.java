package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Linha do tempo da nota de uma análise.
 *
 * @param variacao  diferença entre o penúltimo e o último ponto, no sinal cru do
 *                  score. Em RISCO um score maior é pior — quem traduz isso é
 *                  {@code tendencia}, não o sinal.
 * @param tendencia SEM_DADOS, PRIMEIRA_MEDICAO, MELHORANDO, PIORANDO ou ESTAVEL
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
