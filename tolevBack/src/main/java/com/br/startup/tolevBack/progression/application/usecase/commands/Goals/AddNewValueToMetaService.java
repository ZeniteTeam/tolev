package com.br.startup.tolevBack.progression.application.usecase.commands.Goals;

import com.br.startup.tolevBack.progression.application.dto.request.AddValueToMetaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.mapper.MetaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddNewValueToMetaService {

    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional
    public void execute(AddValueToMetaRequest request) {
        Meta meta = metaRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Não encontrada meta."));

        var currentProgress = meta.getProgressoMeta().getProgresso();
        var newProgress = currentProgress.add(request.getValue());

        var progresso = meta.getProgressoMeta();
        progresso.setProgresso(newProgress);
        progressoMetaRepository.save(progresso);
    }
}
