package com.br.startup.tolevBack.analysis.api.controller;

import com.br.startup.tolevBack.analysis.api.facade.RecommendationFacade;
import com.br.startup.tolevBack.analysis.application.dto.response.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationFacade recommendationFacade;

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(recommendationFacade.getAll(idUsuario));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<RecommendationResponse> acceptRecommendation(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationFacade.accept(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RecommendationResponse> completeRecommendation(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationFacade.complete(id));
    }
}
