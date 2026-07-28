package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProgressionGraphsResponse(
    Long idUsuario,
    BigDecimal progressaoMediaModulos,
    Integer totalModulosConcluidos,
    Integer totalModulosEmProgresso,
    List<DividaProgressPoint> dividasProgresso
) {
    public record DividaProgressPoint(Long idDivida, String nomeDivida, BigDecimal progresso, String status) {}
}
