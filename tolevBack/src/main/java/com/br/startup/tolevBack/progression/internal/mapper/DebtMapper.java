package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;

public class DebtMapper {

    public static DebtProjectionResponse toProjectionResponse(Divida divida, ProgressoDivida progresso) {
        return new DebtProjectionResponse(
                divida.getId(),
                divida.getValorDivida(),
                divida.getStatus(),
                progresso != null ? progresso.getProgresso() : null,
                progresso != null ? progresso.getPeso() : null,
                progresso != null ? progresso.getUltimoProgresso() : null
        );
    }
}
