package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.MapaProgressaoResponse;
import com.br.startup.tolevBack.progression.internal.entity.MapaProgressao;

public class MapaProgressaoMapper {

    public static MapaProgressaoResponse toResponse(MapaProgressao mapa) {
        return new MapaProgressaoResponse(
                mapa.getId(),
                mapa.getNomeMapa(),
                mapa.getUrlModelo()
        );
    }
}
