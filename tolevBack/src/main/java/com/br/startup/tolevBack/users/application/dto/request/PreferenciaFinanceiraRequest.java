package com.br.startup.tolevBack.users.application.dto.request;

import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import com.br.startup.tolevBack.users.internal.enums.MetodoQuitacao;

import java.math.BigDecimal;

/**
 * Atualização das preferências financeiras do usuário. Todos os campos são
 * opcionais: os nulos preservam o valor já salvo (atualização parcial).
 */
public record PreferenciaFinanceiraRequest(
    MetodoQuitacao metodoQuitacao,
    BigDecimal aporteExtraMensal,
    MetodoOrcamento metodoOrcamento,
    BigDecimal rendaMensal,
    Integer percFixos,
    Integer percDividas,
    Integer percLazer,
    BigDecimal reservaEmergenciaMeta
) {}
