package com.br.startup.tolevBack.analysis.internal.mapper;

import com.br.startup.tolevBack.analysis.application.dto.response.RecommendationResponse;
import com.br.startup.tolevBack.analysis.internal.entity.Recomendacao;

public class RecommendationMapper {

    public static RecommendationResponse toResponse(Recomendacao recomendacao) {
        return new RecommendationResponse(
                recomendacao.getId(),
                recomendacao.getIdUsuario(),
                recomendacao.getAnalise() != null ? recomendacao.getAnalise().getId() : null,
                recomendacao.getTipo(),
                recomendacao.getTitulo(),
                recomendacao.getDescricao(),
                recomendacao.getDificuldade(),
                recomendacao.getPrioridade(),
                recomendacao.getStatus(),
                recomendacao.getDataCriacao()
        );
    }
}
