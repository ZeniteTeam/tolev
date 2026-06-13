package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.RecommendationResponse;
import com.br.startup.tolevBack.analysis.internal.mapper.RecommendationMapper;
import com.br.startup.tolevBack.analysis.internal.repository.IRecomendacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRecommendationsService {

    private final IRecomendacaoRepository recomendacaoRepository;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> execute(Long idUsuario) {
        return recomendacaoRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(RecommendationMapper::toResponse)
                .toList();
    }
}
