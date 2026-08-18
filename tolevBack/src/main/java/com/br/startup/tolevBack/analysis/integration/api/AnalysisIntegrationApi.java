package com.br.startup.tolevBack.analysis.integration.api;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisImpactResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisRiskResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisScorePointResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisVariableResponse;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;

import java.util.List;
import java.util.Optional;

/**
 * O que a análise expõe para os outros módulos.
 *
 * <p>O módulo de gráficos consome tudo daqui — nunca os repositórios da análise.
 */
public interface AnalysisIntegrationApi {

    List<AnalysisResponse> getAnalysisByUser(Long idUsuario);

    /** Série temporal da nota de um tipo, em ordem cronológica. */
    List<AnalysisScorePointResponse> getScoreHistory(Long idUsuario, TipoAnalise tipo);

    /** Variáveis da análise mais recente de um tipo, da que mais contribui para a que menos. */
    List<AnalysisVariableResponse> getLatestVariables(Long idUsuario, TipoAnalise tipo);

    /** Achados com custo estimado, do mais caro ao mais barato, sem repetir o mesmo achado. */
    List<AnalysisImpactResponse> getImpacts(Long idUsuario);

    /** A análise de risco mais recente, se já houver alguma. */
    Optional<AnalysisRiskResponse> getLatestRisk(Long idUsuario);
}
