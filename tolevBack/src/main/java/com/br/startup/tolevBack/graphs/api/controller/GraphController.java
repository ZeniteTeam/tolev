package com.br.startup.tolevBack.graphs.api.controller;

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
}
