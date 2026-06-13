package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.graphs.application.dto.response.RiskGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.RiskGraphResponse.RiskDataPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRiskGraphService {

    private final AnalysisIntegrationApi analysisIntegrationApi;

    public RiskGraphResponse execute(Long idUsuario) {
        var analyses = analysisIntegrationApi.getAnalysisByUser(idUsuario);

        String nivelRiscoGeral = analyses.isEmpty() ? "SEM_DADOS" : "BAIXO";

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
