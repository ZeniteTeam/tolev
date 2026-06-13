package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;

public class ProgressaoModuloMapper {

    public static ProgressaoModuloResponse toResponse(ModuloProgressaoUsuario modulo) {
        return new ProgressaoModuloResponse(
                modulo.getId(),
                modulo.getIdUsuario(),
                modulo.getMapaModulo() != null ? modulo.getMapaModulo().getId() : null,
                modulo.getMapaModulo() != null ? modulo.getMapaModulo().getTipo() : null,
                modulo.getMapaModulo() != null ? modulo.getMapaModulo().getEstilo() : null,
                modulo.getProgressao()
        );
    }
}
