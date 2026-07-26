package com.br.startup.tolevBack.progression.application.dto.response;

import java.math.BigDecimal;

/**
 * Uma dívida dentro do plano de quitação, já posicionada na ordem de ataque
 * ditada pelo método escolhido pelo usuário.
 */
public record DebtStrategyItemResponse(
    Long idDivida,
    String nomeDivida,
    String banco,
    BigDecimal saldoRestante,
    BigDecimal taxaJuros,
    BigDecimal parcelaMinima,
    Integer pesoEmocional,
    Integer ordemAtaque,
    boolean foco
) {}
