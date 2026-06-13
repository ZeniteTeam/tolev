package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;

public record ProgressionStatsResponse(
    Long idUsuario,
    Integer totalMetas,
    Integer metasAtivas,
    Integer metasConcluidas,
    Integer metasCanceladas,
    Integer totalDividas,
    Integer dividasAtivas,
    Integer dividasPagas,
    Integer dividasAtrasadas,
    BigDecimal valorTotalDividas,
    Integer totalModulos,
    BigDecimal progressaoMedia
) {}
