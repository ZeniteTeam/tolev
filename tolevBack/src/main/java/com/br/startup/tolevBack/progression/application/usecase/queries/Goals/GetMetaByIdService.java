package com.br.startup.tolevBack.progression.application.usecase.queries.Goals;

import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;
import com.br.startup.tolevBack.progression.internal.mapper.MetaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoMetaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMetaByIdService {

    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional(readOnly = true)
    public MetaResponse execute(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meta não encontrada com id: " + id));
        ProgressoMeta progresso = progressoMetaRepository.findByMeta(meta).orElse(null);
        return MetaMapper.toResponse(meta, progresso);
    }
}
