package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.application.dto.response.MapaProgressaoResponse;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetMapByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetMapModulesService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapFacade {

    private final GetMapsService getMaps;
    private final GetMapByIdService getMapById;
    private final GetMapModulesService getMapModules;

    public List<MapaProgressaoResponse> getAll() {
        return getMaps.execute();
    }

    public MapaProgressaoResponse getById(Long id) {
        return getMapById.execute(id);
    }

    public List<MapaModuloResponse> getModules(Long idMapa) {
        return getMapModules.execute(idMapa);
    }
}
