package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.MapaProgressaoResponse;
import com.br.startup.tolevBack.progression.internal.mapper.MapaProgressaoMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMapaProgressaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMapsService {

    private final IMapaProgressaoRepository mapaProgressaoRepository;

    @Transactional(readOnly = true)
    public List<MapaProgressaoResponse> execute() {
        return mapaProgressaoRepository.findAll()
                .stream()
                .map(MapaProgressaoMapper::toResponse)
                .toList();
    }
}
