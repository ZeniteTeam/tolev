package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProgressionGraphsResponse(
    Long idUsuario,
    BigDecimal progressaoMediaModulos,
    Integer totalModulosConcluidos,
    Integer totalModulosEmProgresso,
    List<MetaProgressPoint> metasProgresso
) {
    public record MetaProgressPoint(Long idMeta, String nomeMeta, BigDecimal progresso, String status) {}
}
