package com.br.startup.tolevBack.analysis.api.controller;

import com.br.startup.tolevBack.analysis.api.facade.AnalysisFacade;
import com.br.startup.tolevBack.analysis.application.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisFacade analysisFacade;

    @GetMapping
    public ResponseEntity<List<AnalysisResponse>> getAnalysis(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getAll(idUsuario));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AnalysisResponse>> getLatestAnalysis(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getLatest(idUsuario));
    }

    @GetMapping("/risks")
    public ResponseEntity<List<AnalysisRiskResponse>> getAnalysisRisks(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getRisks(idUsuario));
    }

    @GetMapping("/financial-health")
    public ResponseEntity<List<FinancialHealthResponse>> getFinancialHealth(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getFinancialHealth(idUsuario));
    }

    @GetMapping("/spending-patterns")
    public ResponseEntity<List<SpendingPatternsResponse>> getSpendingPatterns(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getSpendingPatterns(idUsuario));
    }

    @GetMapping("/debt-analysis")
    public ResponseEntity<List<DebtAnalysisResponse>> getDebtAnalysis(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getDebtAnalysis(idUsuario));
    }
}
