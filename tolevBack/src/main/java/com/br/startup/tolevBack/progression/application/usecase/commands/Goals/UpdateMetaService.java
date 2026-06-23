package com.br.startup.tolevBack.progression.application.usecase.commands.Goals;

import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
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
public class UpdateMetaService {

    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional
    public MetaResponse execute(Long id, MetaRequest request) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meta não encontrada com id: " + id));
        meta.setNomeMeta(request.nomeMeta());
        meta.setValorMeta(request.valorMeta());
        meta.setStatus(request.status());
        meta.setTipo(request.tipo());
        meta.setCategoria(request.categoria());
        meta.setDataLimite(request.dataLimite());
        meta.setRecompensa(request.recompensa());
        meta.setMotivacaoMeta(request.motivacaoMeta());
        Meta saved = metaRepository.save(meta);
        ProgressoMeta progresso = progressoMetaRepository.findByMeta(saved).orElse(null);
        return MetaMapper.toResponse(saved, progresso);
    }
}
