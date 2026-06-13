package com.br.startup.tolevBack.simulations.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.simulations.application.dto.request.FutureBalanceSimulationRequest;
import com.br.startup.tolevBack.simulations.application.dto.response.FutureBalanceSimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SimulateFutureBalanceService {

    private final FinanceIntegrationApi financeIntegrationApi;

    public FutureBalanceSimulationResponse execute(FutureBalanceSimulationRequest request) {
        AccountResponse account = financeIntegrationApi.getAccountById(request.idContaBancaria());
        List<TransactionResponse> transactions = financeIntegrationApi.getTransactionsByUser(request.idUsuario());

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

        BigDecimal variacaoMensal = totalReceitas.subtract(totalDespesas);
        BigDecimal saldoAtual = account.saldoAtual() != null ? account.saldoAtual() : BigDecimal.ZERO;
        BigDecimal saldoProjetado = saldoAtual.add(
                variacaoMensal.multiply(new BigDecimal(request.meses()))
        );

        return new FutureBalanceSimulationResponse(
                request.idContaBancaria(),
                saldoAtual,
                saldoProjetado,
                request.meses(),
                variacaoMensal
        );
    }
}
