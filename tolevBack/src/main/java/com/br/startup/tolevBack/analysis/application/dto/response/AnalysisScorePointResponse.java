package com.br.startup.tolevBack.analysis.application.dto.response;

import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Um ponto da série temporal de uma análise: a nota daquele dia. */
public record AnalysisScorePointResponse(
    LocalDate data,
    TipoAnalise tipo,
    BigDecimal score,
    String classificacao,
    NivelRisco nivelRisco,
    /** Só nos modelos que estimam chance de evento (inadimplência). */
    BigDecimal probabilidade
) {}
