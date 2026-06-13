package com.br.startup.tolevBack.analysis.application.dto.response;

import com.br.startup.tolevBack.analysis.internal.enums.Prioridade;
import com.br.startup.tolevBack.analysis.internal.enums.StatusRecomendacao;
import com.br.startup.tolevBack.analysis.internal.enums.TipoRecomendacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecommendationResponse(
    Long id,
    Long idUsuario,
    Long idAnalise,
    TipoRecomendacao tipo,
    String titulo,
    String descricao,
    BigDecimal dificuldade,
    Prioridade prioridade,
    StatusRecomendacao status,
    LocalDateTime dataCriacao
) {}
