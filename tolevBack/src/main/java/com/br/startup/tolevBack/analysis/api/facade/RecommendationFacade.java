package com.br.startup.tolevBack.analysis.api.facade;

import com.br.startup.tolevBack.analysis.application.dto.response.RecommendationResponse;
import com.br.startup.tolevBack.analysis.application.usecase.commands.AcceptRecommendationService;
import com.br.startup.tolevBack.analysis.application.usecase.commands.CompleteRecommendationService;
import com.br.startup.tolevBack.analysis.application.usecase.queries.GetRecommendationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationFacade {

    private final GetRecommendationsService getRecommendations;
    private final AcceptRecommendationService acceptRecommendation;
    private final CompleteRecommendationService completeRecommendation;

    public List<RecommendationResponse> getAll(Long idUsuario) {
        return getRecommendations.execute(idUsuario);
    }

    public RecommendationResponse accept(Long id) {
        return acceptRecommendation.execute(id);
    }

    public RecommendationResponse complete(Long id) {
        return completeRecommendation.execute(id);
    }
}
