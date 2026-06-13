package com.br.startup.tolevBack.progression.api.controller;

import com.br.startup.tolevBack.progression.api.facade.ProgressionFacade;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionOverviewResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progression")
@RequiredArgsConstructor
public class ProgressionController {

    private final ProgressionFacade progressionFacade;

    @GetMapping("/overview")
    public ResponseEntity<ProgressionOverviewResponse> getOverview(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(progressionFacade.getOverview(idUsuario));
    }

    @GetMapping("/stats")
    public ResponseEntity<ProgressionStatsResponse> getStats(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(progressionFacade.getStats(idUsuario));
    }

    @GetMapping("/graphs")
    public ResponseEntity<ProgressionGraphsResponse> getGraphs(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(progressionFacade.getGraphs(idUsuario));
    }
}
