package com.br.startup.tolevBack.analysis.application.dto.response;

import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancialHealthResponse(
    Long idAnalise,
    Long idUsuario,
    String resultadoResumo,
    NivelRisco nivelRisco,
    BigDecimal score,
    BigDecimal coeficienteGeral,
    String classificacao,
    LocalDateTime dataCriacao
) {}
