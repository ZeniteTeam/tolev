package com.br.startup.tolevBack.graphs.api.facade;

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
}
