package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.graphs.application.dto.response.SpendingGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.SpendingGraphResponse.SpendingDataPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetSpendingGraphService {

    private final FinanceIntegrationApi financeIntegrationApi;

    public SpendingGraphResponse execute(Long idUsuario) {
        List<TransactionResponse> transactions = financeIntegrationApi.getTransactionsByUser(idUsuario);

        BigDecimal totalReceitas = transactions.stream()
                .filter(t -> t.tipo() != null && "RECEITA".equals(t.tipo().name()))
                .map(TransactionResponse::valor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transactions.stream()
                .filter(t -> t.tipo() != null && "DESPESA".equals(t.tipo().name()))
                .map(TransactionResponse::valor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = totalReceitas.add(totalDespesas);
        BigDecimal saldoLiquido = totalReceitas.subtract(totalDespesas);

        List<SpendingDataPoint> pontos = List.of(
                new SpendingDataPoint("RECEITA", totalReceitas, pct(totalReceitas, total)),
                new SpendingDataPoint("DESPESA", totalDespesas, pct(totalDespesas, total))
        );

        return new SpendingGraphResponse(idUsuario, totalReceitas, totalDespesas, saldoLiquido, pontos);
    }

    private BigDecimal pct(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return part.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
