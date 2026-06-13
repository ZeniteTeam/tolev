package com.br.startup.tolevBack.simulations.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.simulations.application.dto.request.PurchaseImpactSimulationRequest;
import com.br.startup.tolevBack.simulations.application.dto.response.PurchaseImpactSimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class SimulatePurchaseImpactService {

    private final FinanceIntegrationApi financeIntegrationApi;

    public PurchaseImpactSimulationResponse execute(PurchaseImpactSimulationRequest request) {
        AccountResponse account = financeIntegrationApi.getAccountById(request.idContaBancaria());

        BigDecimal saldoAtual = account.saldoAtual() != null ? account.saldoAtual() : BigDecimal.ZERO;
        BigDecimal saldoDisponivel = account.saldoDisponivel() != null ? account.saldoDisponivel() : BigDecimal.ZERO;
        BigDecimal valorCompra = request.valorCompra();

        BigDecimal saldoAposCompra = saldoAtual.subtract(valorCompra);
        BigDecimal saldoDisponivelAposCompra = saldoDisponivel.subtract(valorCompra);

        BigDecimal impactoMensal = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.parcelado()) && request.numeroParcelas() != null && request.numeroParcelas() > 0) {
            impactoMensal = valorCompra.divide(new BigDecimal(request.numeroParcelas()), 2, RoundingMode.HALF_UP);
        }

        return new PurchaseImpactSimulationResponse(
                request.idContaBancaria(),
                saldoAtual,
                saldoDisponivel,
                valorCompra,
                saldoAposCompra,
                saldoDisponivelAposCompra,
                request.parcelado(),
                request.numeroParcelas(),
                impactoMensal,
                saldoDisponivelAposCompra.compareTo(BigDecimal.ZERO) >= 0
        );
    }
}
