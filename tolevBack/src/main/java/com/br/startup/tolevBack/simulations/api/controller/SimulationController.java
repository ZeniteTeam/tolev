package com.br.startup.tolevBack.simulations.api.controller;

import com.br.startup.tolevBack.simulations.api.facade.SimulationFacade;
import com.br.startup.tolevBack.simulations.application.dto.request.*;
import com.br.startup.tolevBack.simulations.application.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationFacade simulationFacade;

    @PostMapping("/debt-payoff")
    public ResponseEntity<DebtPayoffSimulationResponse> debtPayoff(@RequestBody DebtPayoffSimulationRequest request) {
        return ResponseEntity.ok(simulationFacade.debtPayoff(request));
    }

    @PostMapping("/future-balance")
    public ResponseEntity<FutureBalanceSimulationResponse> futureBalance(@RequestBody FutureBalanceSimulationRequest request) {
        return ResponseEntity.ok(simulationFacade.futureBalance(request));
    }

    @PostMapping("/purchase-impact")
    public ResponseEntity<PurchaseImpactSimulationResponse> purchaseImpact(@RequestBody PurchaseImpactSimulationRequest request) {
        return ResponseEntity.ok(simulationFacade.purchaseImpact(request));
    }

    @PostMapping("/meta-projection")
    public ResponseEntity<MetaProjectionSimulationResponse> metaProjection(@RequestBody MetaProjectionSimulationRequest request) {
        return ResponseEntity.ok(simulationFacade.metaProjection(request));
    }

    @PostMapping("/financial-future")
    public ResponseEntity<FinancialFutureSimulationResponse> financialFuture(@RequestBody FinancialFutureSimulationRequest request) {
        return ResponseEntity.ok(simulationFacade.financialFuture(request));
    }
}
