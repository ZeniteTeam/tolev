package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;

public record ProgressionOverviewResponse(
    Long idUsuario,
    Integer totalModulos,
    BigDecimal progressaoMedia,
    Integer totalDividas,
    Integer dividasAtivas,
    Integer dividasAtrasadas
) {}
