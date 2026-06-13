package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import com.br.startup.tolevBack.progression.application.usecase.commands.CompleteModuleService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetModuleByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModuleFacade {

    private final GetModuleByIdService getModuleById;
    private final CompleteModuleService completeModule;

    public MapaModuloResponse getById(Long id) {
        return getModuleById.execute(id);
    }

    public ProgressaoModuloResponse complete(Long id, Long idUsuario) {
        return completeModule.execute(id, idUsuario);
    }
}
