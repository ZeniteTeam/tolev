package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.EstiloModulo;
import com.br.startup.tolevBack.progression.internal.enums.TipoModulo;

import java.math.BigDecimal;

public record MapaModuloResponse(
    Long id,
    Long idMapaProgressao,
    TipoModulo tipo,
    EstiloModulo estilo,
    BigDecimal requisitos,
    BigDecimal posX,
    BigDecimal posY
) {}
