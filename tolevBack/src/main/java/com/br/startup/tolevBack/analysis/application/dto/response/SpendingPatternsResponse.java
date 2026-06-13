package com.br.startup.tolevBack.analysis.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SpendingPatternsResponse(
    Long idAnalise,
    Long idUsuario,
    String resultadoResumo,
    BigDecimal score,
    String classificacao,
    LocalDateTime dataCriacao
) {}
