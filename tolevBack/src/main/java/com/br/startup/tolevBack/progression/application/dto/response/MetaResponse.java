package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.enums.TipoMeta;

import java.math.BigDecimal;

public record MetaResponse(
    Long id,
    Long idUsuario,
    String nomeMeta,
    BigDecimal valorMeta,
    StatusMeta status,
    TipoMeta tipo,
    BigDecimal progresso
) {}
