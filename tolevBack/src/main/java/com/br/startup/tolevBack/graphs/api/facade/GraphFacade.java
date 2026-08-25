package com.br.startup.tolevBack.graphs.api.facade;

import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.graphs.application.dto.response.*;
import com.br.startup.tolevBack.graphs.application.usecase.queries.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphFacade {

    private final GetSpendingGraphService getSpendingGraph;
    private final GetDebtEvolutionGraphService getDebtEvolutionGraph;
    private final GetProgressionGraphService getProgressionGraph;
    private final GetRiskGraphService getRiskGraph;
    private final GetScoreEvolutionGraphService getScoreEvolutionGraph;
    private final GetScoreDriversGraphService getScoreDriversGraph;
    private final GetImpactRankingGraphService getImpactRankingGraph;

    private final GetSpendingByCategory getSpendingByCategory;


    public SpendingGraphResponse getSpending(Long idUsuario) {
        return getSpendingGraph.execute(idUsuario);
    }

    public DebtEvolutionGraphResponse getDebtEvolution(Long idUsuario) {
        return getDebtEvolutionGraph.execute(idUsuario);
    }

    public ProgressionGraphResponse getProgression(Long idUsuario) {
        return getProgressionGraph.execute(idUsuario);
    }

    public RiskGraphResponse getRisk(Long idUsuario) {
        return getRiskGraph.execute(idUsuario);
    }

    public ScoreEvolutionGraphResponse getScoreEvolution(Long idUsuario, TipoAnalise tipo) {
        return getScoreEvolutionGraph.execute(idUsuario, tipo);
    }

    public ScoreDriversGraphResponse getScoreDrivers(Long idUsuario, TipoAnalise tipo) {
        return getScoreDriversGraph.execute(idUsuario, tipo);
    }

    public ImpactRankingGraphResponse getImpactRanking(Long idUsuario) {
        return getImpactRankingGraph.execute(idUsuario);
    }

    public SpendingByCategoryGraphResponse getSpendingByCategory(Long idUsuario, int meses) {
        return getSpendingByCategory.execute(idUsuario, meses);
    }
}
