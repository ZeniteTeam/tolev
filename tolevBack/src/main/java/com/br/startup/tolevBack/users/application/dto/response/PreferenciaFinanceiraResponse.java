package com.br.startup.tolevBack.users.application.dto.response;

import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import com.br.startup.tolevBack.users.internal.enums.MetodoQuitacao;

import java.math.BigDecimal;

public record PreferenciaFinanceiraResponse(
    Long idUsuario,
    MetodoQuitacao metodoQuitacao,
    BigDecimal aporteExtraMensal,
    MetodoOrcamento metodoOrcamento,
    BigDecimal rendaMensal,
    Integer percFixos,
    Integer percDividas,
    Integer percLazer,
    BigDecimal reservaEmergenciaMeta
) {}
