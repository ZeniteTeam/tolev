package com.br.startup.tolevBack.analysis.integration.api;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;

import java.util.List;

public interface AnalysisIntegrationApi {
    List<AnalysisResponse> getAnalysisByUser(Long idUsuario);
}
