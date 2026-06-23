package com.br.startup.tolevBack.progression.application.usecase.queries.Goals;

import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;
import com.br.startup.tolevBack.progression.internal.mapper.MetaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMetasByUserService {

    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional(readOnly = true)
    public List<MetaResponse> execute(Long idUsuario) {
        return metaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(meta -> {
                    ProgressoMeta progresso = progressoMetaRepository.findByMeta(meta).orElse(null);
                    return MetaMapper.toResponse(meta, progresso);
                })
                .toList();
    }
}
