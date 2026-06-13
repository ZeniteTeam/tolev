package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.MapaProgressaoResponse;
import com.br.startup.tolevBack.progression.internal.mapper.MapaProgressaoMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMapaProgressaoRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMapByIdService {

    private final IMapaProgressaoRepository mapaProgressaoRepository;

    @Transactional(readOnly = true)
    public MapaProgressaoResponse execute(Long id) {
        return mapaProgressaoRepository.findById(id)
                .map(MapaProgressaoMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Mapa não encontrado com id: " + id));
    }
}
