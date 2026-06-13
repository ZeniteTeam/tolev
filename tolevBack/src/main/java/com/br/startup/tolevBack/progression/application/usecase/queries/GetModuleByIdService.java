package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.internal.mapper.MapaModuloMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMapaModuloRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetModuleByIdService {

    private final IMapaModuloRepository mapaModuloRepository;

    @Transactional(readOnly = true)
    public MapaModuloResponse execute(Long id) {
        return mapaModuloRepository.findById(id)
                .map(MapaModuloMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado com id: " + id));
    }
}
