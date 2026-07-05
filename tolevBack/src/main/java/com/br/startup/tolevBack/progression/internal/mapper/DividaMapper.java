package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;

import java.util.Comparator;
import java.util.List;

public class DividaMapper {

    public static DividaResponse toResponse(Divida divida) {
        List<ParcelaResponse> parcelas = divida.getParcelas() == null ? List.of()
                : divida.getParcelas().stream()
                        .sorted(Comparator.comparing(
                                p -> p.getNumeroParcela() == null ? 0 : p.getNumeroParcela()))
                        .map(p -> new ParcelaResponse(
                                p.getId(),
                                p.getNumeroParcela(),
                                p.getValorTotal(),
                                p.getStatus(),
                                p.getDataVencimento(),
                                p.getDataPagamento()))
                        .toList();

        return new DividaResponse(
                divida.getId(),
                divida.getIdUsuario(),
                divida.getNomeDivida(),
                divida.getBanco(),
                divida.getTipo(),
                divida.getValorDivida(),
                divida.getTaxaJuros(),
                divida.getParcelaMinima(),
                divida.getPesoEmocional(),
                divida.getQuantidadeParcelas(),
                parcelas
        );
    }
}
