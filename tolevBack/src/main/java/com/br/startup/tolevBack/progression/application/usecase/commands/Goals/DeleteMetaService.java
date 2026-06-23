package com.br.startup.tolevBack.progression.application.usecase.commands.Goals;

import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteMetaService {

    private final IMetaRepository metaRepository;

    @Transactional
    public void execute(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meta não encontrada com id: " + id));
        metaRepository.delete(meta);
    }
}
