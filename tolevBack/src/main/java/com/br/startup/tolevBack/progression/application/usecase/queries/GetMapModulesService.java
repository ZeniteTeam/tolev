package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.internal.mapper.MapaModuloMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMapaModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMapModulesService {

    private final IMapaModuloRepository mapaModuloRepository;

    @Transactional(readOnly = true)
    public List<MapaModuloResponse> execute(Long idMapaProgressao) {
        return mapaModuloRepository.findByMapaProgressao_Id(idMapaProgressao)
                .stream()
                .map(MapaModuloMapper::toResponse)
                .toList();
    }
}
