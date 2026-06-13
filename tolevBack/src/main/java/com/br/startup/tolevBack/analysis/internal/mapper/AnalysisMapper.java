package com.br.startup.tolevBack.analysis.internal.mapper;

import com.br.startup.tolevBack.analysis.application.dto.response.*;
import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;

public class AnalysisMapper {

    public static AnalysisResponse toResponse(Analise analise) {
        return new AnalysisResponse(
                analise.getId(),
                analise.getIdUsuario(),
                analise.getTipo(),
                analise.getResultadoResumo(),
                analise.getRelevancia(),
                analise.getStatus(),
                analise.getDataCriacao(),
                analise.getPeriodoInicio(),
                analise.getPeriodoFim(),
                analise.getAcionavel()
        );
    }

    public static AnalysisRiskResponse toRiskResponse(Analise analise, AnaliseResultado resultado) {
        return new AnalysisRiskResponse(
                analise.getId(),
                analise.getIdUsuario(),
                analise.getResultadoResumo(),
                resultado != null ? resultado.getNivelRisco() : null,
                resultado != null ? resultado.getScore() : null,
                resultado != null ? resultado.getClassificacao() : null,
                resultado != null ? resultado.getExplicacao() : null,
                analise.getDataCriacao()
        );
    }

    public static FinancialHealthResponse toHealthResponse(Analise analise, AnaliseResultado resultado) {
        return new FinancialHealthResponse(
                analise.getId(),
                analise.getIdUsuario(),
                analise.getResultadoResumo(),
                resultado != null ? resultado.getNivelRisco() : null,
                resultado != null ? resultado.getScore() : null,
                resultado != null ? resultado.getCoeficienteGeral() : null,
                resultado != null ? resultado.getClassificacao() : null,
                analise.getDataCriacao()
        );
    }

    public static SpendingPatternsResponse toSpendingResponse(Analise analise, AnaliseResultado resultado) {
        return new SpendingPatternsResponse(
                analise.getId(),
                analise.getIdUsuario(),
                analise.getResultadoResumo(),
                resultado != null ? resultado.getScore() : null,
                resultado != null ? resultado.getClassificacao() : null,
                analise.getDataCriacao()
        );
    }

    public static DebtAnalysisResponse toDebtResponse(Analise analise, AnaliseResultado resultado) {
        return new DebtAnalysisResponse(
                analise.getId(),
                analise.getIdUsuario(),
                analise.getResultadoResumo(),
                resultado != null ? resultado.getNivelRisco() : null,
                resultado != null ? resultado.getScore() : null,
                resultado != null ? resultado.getProbabilidade() : null,
                resultado != null ? resultado.getClassificacao() : null,
                analise.getDataCriacao()
        );
    }
}
