package com.br.startup.tolevBack.analysis.application.dto.response;

import com.br.startup.tolevBack.analysis.internal.enums.StatusAnalise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AnalysisResponse(
    Long id,
    Long idUsuario,
    TipoAnalise tipo,
    String resultadoResumo,
    String relevancia,
    StatusAnalise status,
    LocalDateTime dataCriacao,
    LocalDate periodoInicio,
    LocalDate periodoFim,
    Boolean acionavel
) {}
