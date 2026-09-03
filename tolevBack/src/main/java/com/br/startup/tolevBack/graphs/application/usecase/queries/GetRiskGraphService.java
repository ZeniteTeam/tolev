package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisRiskResponse;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.graphs.application.dto.response.RiskGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.RiskGraphResponse.RiskDataPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Painel de risco: o nível consolidado mais recente e os indicadores que o
 * compõem.
 *
 * <p>O nível vem da análise de risco de verdade. Antes ele era fixo em "BAIXO"
 * sempre que houvesse qualquer análise — nada escrevia na tabela, então não
 * havia de onde tirar o valor.
 */
@Service
@RequiredArgsConstructor
public class GetRiskGraphService {

    private final AnalysisIntegrationApi analysisIntegrationApi;

    public RiskGraphResponse execute(Long idUsuario) {
        var analyses = analysisIntegrationApi.getAnalysisByUser(idUsuario);

        String nivelRiscoGeral = analysisIntegrationApi.getLatestRisk(idUsuario)
                .map(AnalysisRiskResponse::nivelRisco)
                .map(Enum::name)
                .orElse(analyses.isEmpty() ? "SEM_DADOS" : "NAO_CALCULADO");

        List<RiskDataPoint> indicadores = analyses.stream()
                .map(a -> new RiskDataPoint(
                        a.tipo() != null ? a.tipo().name() : null,
                        a.resultadoResumo(),
                        a.relevancia(),
                        a.dataCriacao()
                ))
                .toList();

        return new RiskGraphResponse(idUsuario, nivelRiscoGeral, analyses.size(), indicadores);
    }
}
