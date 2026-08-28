package com.br.startup.tolevBack.analysis.api.controller;

import com.br.startup.tolevBack.analysis.api.facade.AnalysisFacade;
import com.br.startup.tolevBack.analysis.application.dto.response.*;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
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

    /** Evolução da nota de um tipo ao longo do tempo. */
    @GetMapping("/score-history")
    public ResponseEntity<List<AnalysisScorePointResponse>> getScoreHistory(
            @RequestParam Long idUsuario,
            @RequestParam TipoAnalise tipo) {
        return ResponseEntity.ok(analysisFacade.getScoreHistory(idUsuario, tipo));
    }

    /** O que compõe a nota atual, da variável que mais pesa para a que menos pesa. */
    @GetMapping("/variables")
    public ResponseEntity<List<AnalysisVariableResponse>> getVariables(
            @RequestParam Long idUsuario,
            @RequestParam TipoAnalise tipo) {
        return ResponseEntity.ok(analysisFacade.getVariables(idUsuario, tipo));
    }

    /** Quanto cada problema detectado custa por mês e por ano. */
    @GetMapping("/impacts")
    public ResponseEntity<List<AnalysisImpactResponse>> getImpacts(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.getImpacts(idUsuario));
    }

    /** Força o recálculo agora, sem esperar o próximo evento. */
    @PostMapping("/generate")
    public ResponseEntity<List<AnalysisResponse>> generate(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(analysisFacade.generate(idUsuario));
    }
}
