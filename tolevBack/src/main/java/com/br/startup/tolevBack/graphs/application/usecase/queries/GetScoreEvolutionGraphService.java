package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisScorePointResponse;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreEvolutionGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreEvolutionGraphResponse.ScorePoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Como a nota do usuário andou ao longo do tempo.
 *
 * <p>Um ponto por dia, direto das análises persistidas — nada é recalculado
 * aqui, o gráfico mostra exatamente o que estava valendo em cada dia.
 */
@Service
@RequiredArgsConstructor
public class GetScoreEvolutionGraphService {

    /** Abaixo disso a variação é ruído de arredondamento, não tendência. */
    private static final BigDecimal LIMIAR_TENDENCIA = new BigDecimal("2");

    private final AnalysisIntegrationApi analysisApi;

    public ScoreEvolutionGraphResponse execute(Long idUsuario, TipoAnalise tipo) {
        List<AnalysisScorePointResponse> historico = analysisApi.getScoreHistory(idUsuario, tipo);

        if (historico.isEmpty()) {
            return new ScoreEvolutionGraphResponse(
                    idUsuario, tipo.name(), null, null, BigDecimal.ZERO, "SEM_DADOS", null, List.of());
        }

        AnalysisScorePointResponse atual = historico.get(historico.size() - 1);
        AnalysisScorePointResponse anterior = historico.size() > 1
                ? historico.get(historico.size() - 2)
                : null;

        BigDecimal scoreAtual = valor(atual.score());
        BigDecimal scoreAnterior = anterior != null ? valor(anterior.score()) : null;
        BigDecimal variacao = scoreAnterior != null
                ? scoreAtual.subtract(scoreAnterior)
                : BigDecimal.ZERO;

        List<ScorePoint> pontos = historico.stream()
                .map(p -> new ScorePoint(
                        p.data(),
                        p.score(),
                        p.classificacao(),
                        p.nivelRisco() != null ? p.nivelRisco().name() : null))
                .toList();

        return new ScoreEvolutionGraphResponse(
                idUsuario,
                tipo.name(),
                scoreAtual,
                scoreAnterior,
                variacao,
                tendencia(variacao, anterior != null),
                atual.classificacao(),
                pontos);
    }

    private String tendencia(BigDecimal variacao, boolean temComparativo) {
        if (!temComparativo) {
            return "PRIMEIRA_MEDICAO";
        }
        if (variacao.compareTo(LIMIAR_TENDENCIA) > 0) {
            return "MELHORANDO";
        }
        if (variacao.compareTo(LIMIAR_TENDENCIA.negate()) < 0) {
            return "PIORANDO";
        }
        return "ESTAVEL";
    }

    private BigDecimal valor(BigDecimal score) {
        return score != null ? score : BigDecimal.ZERO;
    }
}
