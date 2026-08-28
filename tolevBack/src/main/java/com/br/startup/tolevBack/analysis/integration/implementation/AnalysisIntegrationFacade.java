package com.br.startup.tolevBack.analysis.integration.implementation;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisImpactResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisRiskResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisScorePointResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisVariableResponse;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisImpactsService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisRisksService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisScoreHistoryService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisVariablesService;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisIntegrationFacade implements AnalysisIntegrationApi {

    private final GetAnalysisService getAnalysis;
    private final GetAnalysisScoreHistoryService getScoreHistory;
    private final GetAnalysisVariablesService getVariables;
    private final GetAnalysisImpactsService getImpacts;
    private final GetAnalysisRisksService getRisks;

    @Override
    public List<AnalysisResponse> getAnalysisByUser(Long idUsuario) {
        return getAnalysis.execute(idUsuario);
    }

    @Override
    public List<AnalysisScorePointResponse> getScoreHistory(Long idUsuario, TipoAnalise tipo) {
        return getScoreHistory.execute(idUsuario, tipo);
    }

    @Override
    public List<AnalysisVariableResponse> getLatestVariables(Long idUsuario, TipoAnalise tipo) {
        return getVariables.execute(idUsuario, tipo);
    }

    @Override
    public List<AnalysisImpactResponse> getImpacts(Long idUsuario) {
        return getImpacts.execute(idUsuario);
    }

    @Override
    public Optional<AnalysisRiskResponse> getLatestRisk(Long idUsuario) {
        return getRisks.execute(idUsuario)
                .stream()
                .filter(r -> r.dataCriacao() != null)
                .max(Comparator.comparing(AnalysisRiskResponse::dataCriacao));
    }
}
