package com.br.startup.tolevBack.analysis.application.dto.response;

import com.br.startup.tolevBack.analysis.internal.enums.TipoImpacto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Quanto um problema específico custa ao usuário.
 *
 * @param regra           qual regra detectou; identidade estável do achado
 * @param impactoAnual    custo projetado em 12 meses se nada mudar
 */
public record AnalysisImpactResponse(
    Long id,
    String regra,
    TipoImpacto tipoImpacto,
    String entidadeTipo,
    Long entidadeId,
    String descricao,
    String gravidade,
    BigDecimal scoreImpacto,
    BigDecimal impactoEstimadoValor,
    BigDecimal impactoMensal,
    BigDecimal impactoAnual,
    LocalDateTime dataCriacao
) {}
