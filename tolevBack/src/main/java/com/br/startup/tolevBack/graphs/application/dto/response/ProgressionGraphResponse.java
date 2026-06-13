package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProgressionGraphResponse(
    Long idUsuario,
    Integer totalModulos,
    BigDecimal progressaoMedia,
    List<ModuloDataPoint> modulos
) {
    public record ModuloDataPoint(Long idModulo, String tipoModulo, String estiloModulo, BigDecimal progressao) {}
}
