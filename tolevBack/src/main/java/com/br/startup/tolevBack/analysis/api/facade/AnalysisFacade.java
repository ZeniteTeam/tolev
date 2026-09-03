package com.br.startup.tolevBack.analysis.api.facade;

import com.br.startup.tolevBack.analysis.application.dto.response.*;
import com.br.startup.tolevBack.analysis.application.usecase.commands.GenerateAnalysisService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.*;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.analysis.internal.mapper.AnalysisMapper;
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
    private final GetAnalysisScoreHistoryService getScoreHistory;
    private final GetAnalysisVariablesService getVariables;
    private final GetAnalysisImpactsService getImpacts;
    private final GenerateAnalysisService generateAnalysis;

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

    public List<AnalysisScorePointResponse> getScoreHistory(Long idUsuario, TipoAnalise tipo) {
        return getScoreHistory.execute(idUsuario, tipo);
    }

    public List<AnalysisVariableResponse> getVariables(Long idUsuario, TipoAnalise tipo) {
        return getVariables.execute(idUsuario, tipo);
    }

    public List<AnalysisImpactResponse> getImpacts(Long idUsuario) {
        return getImpacts.execute(idUsuario);
    }

    /**
     * Recalcula na hora, ignorando o debounce. O caminho normal é o evento —
     * isto existe para o app oferecer "atualizar análise" e para conseguir ver
     * o resultado sem precisar lançar uma transação de mentira.
     */
    public List<AnalysisResponse> generate(Long idUsuario) {
        return generateAnalysis.execute(idUsuario, true)
                .stream()
                .map(AnalysisMapper::toResponse)
                .toList();
    }
}
