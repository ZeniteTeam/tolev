package com.br.startup.tolevBack.analysis.integration.implementation;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetAnalysisService;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisIntegrationFacade implements AnalysisIntegrationApi {

    private final GetAnalysisService getAnalysis;

    @Override
    public List<AnalysisResponse> getAnalysisByUser(Long idUsuario) {
        return getAnalysis.execute(idUsuario);
    }
}
