package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.internal.entity.TransacaoRecorrente;

public final class GastoFixoMapper {

    private GastoFixoMapper() {}

    public static GastoFixoResponse toResponse(TransacaoRecorrente t) {
        return new GastoFixoResponse(
                t.getId(),
                t.getIdUsuario(),
                t.getDescricao(),
                t.getValor(),
                t.getDiaRecorrencia(),
                t.getConfirmadoEm());
    }
}
