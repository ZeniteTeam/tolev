package com.br.startup.tolevBack.progression.application.usecase.commands.Goals;

import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;
import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.mapper.MetaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateMetaService {

    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional
    public MetaResponse execute(MetaRequest request) {
        Meta meta = Meta.builder()
                .idUsuario(request.idUsuario())
                .nomeMeta(request.nomeMeta())
                .valorMeta(request.valorMeta())
                .status(request.status() != null ? request.status() : StatusMeta.ATIVA)
                .tipo(request.tipo())
                .categoria(request.categoria())
                .dataLimite(request.dataLimite())
                .recompensa(request.recompensa())
                .motivacaoMeta(request.motivacaoMeta())
                .build();

        meta = metaRepository.save(meta);

        ProgressoMeta progresso = new ProgressoMeta();
        progresso.setMeta(meta);

        progresso = progressoMetaRepository.save(progresso);
        progresso.setProgresso(BigDecimal.valueOf(0.0));
        meta.setProgressoMeta(progresso);

        meta = metaRepository.save(meta);

        return MetaMapper.toResponse(meta, null);
    }
}
