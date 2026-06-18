package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;

public class MetaMapper {

    public static MetaResponse toResponse(Meta meta, ProgressoMeta progresso) {
        return new MetaResponse(
                meta.getId(),
                meta.getIdUsuario(),
                meta.getNomeMeta(),
                meta.getValorMeta(),
                meta.getStatus(),
                meta.getTipo(),
                meta.getCategoria(),
                meta.getDataLimite(),
                meta.getRecompensa(),
                meta.getMotivacaoMeta(),
                progresso != null ? progresso.getProgresso() : null,
                progresso != null ? progresso.getPercentualQuitado() : null
        );
    }
}
