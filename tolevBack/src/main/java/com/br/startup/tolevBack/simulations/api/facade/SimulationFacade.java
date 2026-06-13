package com.br.startup.tolevBack.simulations.api.facade;

import com.br.startup.tolevBack.simulations.application.dto.request.*;
import com.br.startup.tolevBack.simulations.application.dto.response.*;
import com.br.startup.tolevBack.simulations.application.usecase.commands.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationFacade {

    private final SimulateDebtPayoffService simulateDebtPayoff;
    private final SimulateFutureBalanceService simulateFutureBalance;
    private final SimulatePurchaseImpactService simulatePurchaseImpact;
    private final SimulateMetaProjectionService simulateMetaProjection;
    private final SimulateFinancialFutureService simulateFinancialFuture;

    public DebtPayoffSimulationResponse debtPayoff(DebtPayoffSimulationRequest request) {
        return simulateDebtPayoff.execute(request);
    }

    public FutureBalanceSimulationResponse futureBalance(FutureBalanceSimulationRequest request) {
        return simulateFutureBalance.execute(request);
    }

    public PurchaseImpactSimulationResponse purchaseImpact(PurchaseImpactSimulationRequest request) {
        return simulatePurchaseImpact.execute(request);
    }

    public MetaProjectionSimulationResponse metaProjection(MetaProjectionSimulationRequest request) {
        return simulateMetaProjection.execute(request);
    }

    public FinancialFutureSimulationResponse financialFuture(FinancialFutureSimulationRequest request) {
        return simulateFinancialFuture.execute(request);
    }
}
