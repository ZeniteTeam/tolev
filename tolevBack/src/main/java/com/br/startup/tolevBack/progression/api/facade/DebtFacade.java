package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.response.DebtPayoffEstimateResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtRiskResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtStrategyPlanResponse;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtPayoffEstimateService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtProjectionService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtRiskService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDebtStrategyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtFacade {

    private final GetDebtProjectionService getDebtProjection;
    private final GetDebtPayoffEstimateService getDebtPayoffEstimate;
    private final GetDebtRiskService getDebtRisk;
    private final GetDebtStrategyPlanService getDebtStrategyPlan;

    public List<DebtProjectionResponse> getProjection(Long idUsuario) {
        return getDebtProjection.execute(idUsuario);
    }

    public DebtPayoffEstimateResponse getPayoffEstimate(Long idUsuario) {
        return getDebtPayoffEstimate.execute(idUsuario);
    }

    public DebtRiskResponse getRisk(Long idUsuario) {
        return getDebtRisk.execute(idUsuario);
    }

    public DebtStrategyPlanResponse getStrategyPlan(Long idUsuario) {
        return getDebtStrategyPlan.execute(idUsuario);
    }
}
