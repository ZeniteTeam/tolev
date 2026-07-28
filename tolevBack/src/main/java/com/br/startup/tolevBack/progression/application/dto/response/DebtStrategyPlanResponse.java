package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Plano de quitação das dívidas do usuário, ordenado conforme o método
 * escolhido (avalanche / bola de neve / tsunami). Alimenta as telas de
 * planejamento, projeções e as recomendações.
 */
public record DebtStrategyPlanResponse(
    Long idUsuario,
    String metodo,
    String criterio,
    BigDecimal aporteExtraMensal,
    BigDecimal totalRestante,
    List<DebtStrategyItemResponse> itens
) {}
