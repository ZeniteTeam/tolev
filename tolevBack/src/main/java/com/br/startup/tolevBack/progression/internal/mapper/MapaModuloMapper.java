package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.MapaModuloResponse;
import com.br.startup.tolevBack.progression.internal.entity.MapaModulo;

public class MapaModuloMapper {

    public static MapaModuloResponse toResponse(MapaModulo modulo) {
        return new MapaModuloResponse(
                modulo.getId(),
                modulo.getMapaProgressao() != null ? modulo.getMapaProgressao().getId() : null,
                modulo.getTipo(),
                modulo.getEstilo(),
                modulo.getRequisitos(),
                modulo.getPosX(),
                modulo.getPosY()
        );
    }
}
