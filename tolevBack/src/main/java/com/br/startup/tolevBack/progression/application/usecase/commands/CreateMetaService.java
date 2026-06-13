package com.br.startup.tolevBack.progression.application.usecase.commands;

import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.mapper.MetaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateMetaService {

    private final IMetaRepository metaRepository;

    @Transactional
    public MetaResponse execute(MetaRequest request) {
        Meta meta = Meta.builder()
                .idUsuario(request.idUsuario())
                .nomeMeta(request.nomeMeta())
                .valorMeta(request.valorMeta())
                .status(request.status())
                .tipo(request.tipo())
                .build();
        return MetaMapper.toResponse(metaRepository.save(meta), null);
    }
}
