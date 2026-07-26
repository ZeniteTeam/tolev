package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.DebtFacade;
import com.br.startup.tolevBack.progression.application.dto.response.DebtPayoffEstimateResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtRiskResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtStrategyPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtFacade debtFacade;

    @GetMapping("/projection")
    public ResponseEntity<List<DebtProjectionResponse>> getProjection(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(debtFacade.getProjection(idUsuario));
    }

    @GetMapping("/payoff-estimate")
    public ResponseEntity<DebtPayoffEstimateResponse> getPayoffEstimate(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(debtFacade.getPayoffEstimate(idUsuario));
    }

    @GetMapping("/risk")
    public ResponseEntity<DebtRiskResponse> getRisk(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(debtFacade.getRisk(idUsuario));
    }

    @GetMapping("/strategy-plan")
    public ResponseEntity<DebtStrategyPlanResponse> getStrategyPlan(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(debtFacade.getStrategyPlan(idUsuario));
    }
}
