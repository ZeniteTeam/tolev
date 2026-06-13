package com.br.startup.tolevBack.simulations.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaProjectionSimulationResponse(
    Long idMeta,
    String nomeMeta,
    BigDecimal valorMeta,
    BigDecimal progressoAtual,
    BigDecimal valorRestante,
    BigDecimal contribuicaoMensal,
    Integer mesesParaAlcancar,
    LocalDate dataPrevista
) {}
