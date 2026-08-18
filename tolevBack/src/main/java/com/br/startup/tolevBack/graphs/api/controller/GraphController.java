package com.br.startup.tolevBack.graphs.api.controller;

import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.graphs.api.facade.GraphFacade;
import com.br.startup.tolevBack.graphs.application.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graphs")
@RequiredArgsConstructor
public class GraphController {

    private final GraphFacade graphFacade;

    @GetMapping("/spending")
    public ResponseEntity<SpendingGraphResponse> getSpending(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(graphFacade.getSpending(idUsuario));
    }

    @GetMapping("/debt-evolution")
    public ResponseEntity<DebtEvolutionGraphResponse> getDebtEvolution(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(graphFacade.getDebtEvolution(idUsuario));
    }

    @GetMapping("/progression")
    public ResponseEntity<ProgressionGraphResponse> getProgression(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(graphFacade.getProgression(idUsuario));
    }

    @GetMapping("/risk")
    public ResponseEntity<RiskGraphResponse> getRisk(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(graphFacade.getRisk(idUsuario));
    }

    /** Evolução da nota no tempo — um ponto por dia analisado. */
    @GetMapping("/score-evolution")
    public ResponseEntity<ScoreEvolutionGraphResponse> getScoreEvolution(
            @RequestParam Long idUsuario,
            @RequestParam(defaultValue = "SAUDE_FINANCEIRA") TipoAnalise tipo) {
        return ResponseEntity.ok(graphFacade.getScoreEvolution(idUsuario, tipo));
    }

    /** O que compõe a nota atual: contribuição de cada indicador. */
    @GetMapping("/score-drivers")
    public ResponseEntity<ScoreDriversGraphResponse> getScoreDrivers(
            @RequestParam Long idUsuario,
            @RequestParam(defaultValue = "SAUDE_FINANCEIRA") TipoAnalise tipo) {
        return ResponseEntity.ok(graphFacade.getScoreDrivers(idUsuario, tipo));
    }

    /** Quanto cada problema detectado custa por ano, do mais caro ao mais barato. */
    @GetMapping("/impact-ranking")
    public ResponseEntity<ImpactRankingGraphResponse> getImpactRanking(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(graphFacade.getImpactRanking(idUsuario));
    }
}
