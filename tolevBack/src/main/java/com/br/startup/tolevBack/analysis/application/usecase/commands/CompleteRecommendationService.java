package com.br.startup.tolevBack.analysis.application.usecase.commands;

import com.br.startup.tolevBack.analysis.application.dto.response.RecommendationResponse;
import com.br.startup.tolevBack.analysis.internal.entity.Recomendacao;
import com.br.startup.tolevBack.analysis.internal.enums.StatusRecomendacao;
import com.br.startup.tolevBack.analysis.internal.mapper.RecommendationMapper;
import com.br.startup.tolevBack.analysis.internal.repository.IRecomendacaoRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompleteRecommendationService {

    private final IRecomendacaoRepository recomendacaoRepository;

    @Transactional
    public RecommendationResponse execute(Long id) {
        Recomendacao recomendacao = recomendacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recomendação não encontrada com id: " + id));
        recomendacao.setStatus(StatusRecomendacao.CONCLUIDA);
        return RecommendationMapper.toResponse(recomendacaoRepository.save(recomendacao));
    }
}
