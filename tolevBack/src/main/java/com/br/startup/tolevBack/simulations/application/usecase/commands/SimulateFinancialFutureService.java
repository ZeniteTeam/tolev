package com.br.startup.tolevBack.simulations.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.simulations.application.dto.request.FinancialFutureSimulationRequest;
import com.br.startup.tolevBack.simulations.application.dto.response.FinancialFutureSimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SimulateFinancialFutureService {

    private final FinanceIntegrationApi financeIntegrationApi;

    public FinancialFutureSimulationResponse execute(FinancialFutureSimulationRequest request) {
        FinancialOverviewResponse overview = financeIntegrationApi.getFinancialOverview(request.idUsuario());

        BigDecimal saldoAtual = overview.totalSaldo() != null ? overview.totalSaldo() : BigDecimal.ZERO;
        BigDecimal receita = overview.mediaReceita() != null ? overview.mediaReceita() : BigDecimal.ZERO;
        BigDecimal despesa = overview.mediaDespesa() != null ? overview.mediaDespesa() : BigDecimal.ZERO;

        BigDecimal multiplicador = switch (request.cenario() != null ? request.cenario() : "REALISTA") {
            case "OTIMISTA" -> new BigDecimal("1.20");
            case "PESSIMISTA" -> new BigDecimal("0.80");
            default -> BigDecimal.ONE;
        };

        BigDecimal receitaAjustada = receita.multiply(multiplicador);
        BigDecimal balanceMensal = receitaAjustada.subtract(despesa);
        BigDecimal mesesBD = new BigDecimal(request.meses());
        BigDecimal saldoProjetado = saldoAtual.add(balanceMensal.multiply(mesesBD));

        return new FinancialFutureSimulationResponse(
                request.idUsuario(),
                request.cenario(),
                request.meses(),
                saldoAtual,
                saldoProjetado,
                receitaAjustada.multiply(mesesBD),
                despesa.multiply(mesesBD),
                balanceMensal
        );
    }
}
