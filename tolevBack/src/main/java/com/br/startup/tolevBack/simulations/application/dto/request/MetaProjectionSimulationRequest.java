package com.br.startup.tolevBack.simulations.application.dto.request;

import java.math.BigDecimal;

public record MetaProjectionSimulationRequest(
    Long idUsuario,
    Long idMeta,
    BigDecimal contribuicaoMensal
) {}
