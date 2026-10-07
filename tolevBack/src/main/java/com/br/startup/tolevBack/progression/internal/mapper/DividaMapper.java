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
        //
        // A referência é `valorContratado`, nunca `valorDivida`: este último é
        // o saldo corrente, que os pagamentos consomem e a quitação zera. Usá-lo
        // aqui fazia o custo dos juros crescer a cada parcela paga e, na dívida
        // quitada, igualar o total da dívida inteira.
        BigDecimal totalAPagar = soma(ordenadas, ParcelaDivida::getValorTotal);
        BigDecimal contratado = contratado(divida, ordenadas);
        BigDecimal totalJuros = totalAPagar.subtract(contratado).max(BigDecimal.ZERO);

        return new DividaResponse(
                divida.getId(),
                divida.getIdUsuario(),
                divida.getNomeDivida(),
                divida.getBanco(),
                divida.getTipo(),
                divida.getStatus(),
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

    /**
     * O principal contratado. Dívida gravada antes da coluna existir cai na
     * soma do principal das parcelas, que também é imutável.
     */
    private static BigDecimal contratado(Divida divida, List<ParcelaDivida> parcelas) {
        if (divida.getValorContratado() != null) {
            return divida.getValorContratado();
        }
        BigDecimal somaPrincipal = soma(parcelas, ParcelaDivida::getValorPrincipal);
        if (somaPrincipal.signum() > 0) {
            return somaPrincipal;
        }
        return divida.getValorDivida() != null ? divida.getValorDivida() : BigDecimal.ZERO;
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
