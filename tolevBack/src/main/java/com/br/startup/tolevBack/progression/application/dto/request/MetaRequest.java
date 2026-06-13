package com.br.startup.tolevBack.progression.application.dto.request;

import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.enums.TipoMeta;

import java.math.BigDecimal;

public record MetaRequest(
    Long idUsuario,
    String nomeMeta,
    BigDecimal valorMeta,
    StatusMeta status,
    TipoMeta tipo
) {}
