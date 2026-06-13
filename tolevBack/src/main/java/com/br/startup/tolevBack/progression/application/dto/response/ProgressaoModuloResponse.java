package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.EstiloModulo;
import com.br.startup.tolevBack.progression.internal.enums.TipoModulo;

import java.math.BigDecimal;

public record ProgressaoModuloResponse(
    Long id,
    Long idUsuario,
    Long idMapaModulo,
    TipoModulo tipoModulo,
    EstiloModulo estiloModulo,
    BigDecimal progressao
) {}
