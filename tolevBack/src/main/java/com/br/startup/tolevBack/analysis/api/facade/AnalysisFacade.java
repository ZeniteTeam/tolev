package com.br.startup.tolevBack.analysis.api.facade;

import com.br.startup.tolevBack.analysis.application.dto.response.*;
import com.br.startup.tolevBack.analysis.application.usecase.queries.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisFacade {

    private final GetAnalysisService getAnalysis;
    private final GetLatestAnalysisService getLatestAnalysis;
    private final GetAnalysisRisksService getAnalysisRisks;
    private final GetFinancialHealthService getFinancialHealth;
    private final GetSpendingPatternsService getSpendingPatterns;
    private final GetDebtAnalysisService getDebtAnalysis;

    public List<AnalysisResponse> getAll(Long idUsuario) {
        return getAnalysis.execute(idUsuario);
    }

    public List<AnalysisResponse> getLatest(Long idUsuario) {
        return getLatestAnalysis.execute(idUsuario);
    }

    public List<AnalysisRiskResponse> getRisks(Long idUsuario) {
        return getAnalysisRisks.execute(idUsuario);
    }

    public List<FinancialHealthResponse> getFinancialHealth(Long idUsuario) {
        return getFinancialHealth.execute(idUsuario);
    }

    public List<SpendingPatternsResponse> getSpendingPatterns(Long idUsuario) {
        return getSpendingPatterns.execute(idUsuario);
    }

    public List<DebtAnalysisResponse> getDebtAnalysis(Long idUsuario) {
        return getDebtAnalysis.execute(idUsuario);
    }
}
