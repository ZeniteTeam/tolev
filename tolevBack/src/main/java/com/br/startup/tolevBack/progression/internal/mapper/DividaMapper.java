package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class DividaMapper {

    public static DividaResponse toResponse(Divida divida) {
        List<ParcelaDivida> ordenadas = divida.getParcelas() == null ? List.of()
                : divida.getParcelas().stream()
                        .sorted(Comparator.comparing(
                                p -> p.getNumeroParcela() == null ? 0 : p.getNumeroParcela()))
                        .toList();

        List<ParcelaResponse> parcelas = ordenadas.stream()
                .map(p -> new ParcelaResponse(
                        p.getId(),
                        p.getNumeroParcela(),
                        p.getValorTotal(),
                        p.getValorPrincipal(),
                        p.getValorJuros(),
                        p.getStatus(),
                        p.getDataVencimento(),
                        p.getDataPagamento()))
                .toList();

        // Tudo que passa do valor contratado é custo — inclusive o ajuste de
        // primeiro período, que entra na tabela como saldo devedor maior e não
        // como juros de uma parcela específica.
        BigDecimal saldo = divida.getValorDivida() != null ? divida.getValorDivida() : BigDecimal.ZERO;
        BigDecimal totalAPagar = soma(ordenadas, ParcelaDivida::getValorTotal);
        BigDecimal totalJuros = totalAPagar.subtract(saldo).max(BigDecimal.ZERO);

        return new DividaResponse(
                divida.getId(),
                divida.getIdUsuario(),
                divida.getNomeDivida(),
                divida.getBanco(),
                divida.getTipo(),
                divida.getValorDivida(),
                divida.getTaxaJuros(),
                divida.getMultaAtraso(),
                divida.getJurosMora(),
                divida.getParcelaMinima(),
                divida.getPesoEmocional(),
                divida.getQuantidadeParcelas(),
                divida.getDataLiberacao(),
                divida.getDataPrimeiroVencimento(),
                divida.getSistemaAmortizacao(),
                divida.getRegimeJuros(),
                totalJuros,
                totalAPagar,
                parcelas
        );
    }

    private static BigDecimal soma(
            List<ParcelaDivida> parcelas,
            java.util.function.Function<ParcelaDivida, BigDecimal> campo) {
        return parcelas.stream()
                .map(campo)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
