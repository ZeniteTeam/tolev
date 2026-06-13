package com.br.startup.tolevBack.simulations.application.usecase.commands;

import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import com.br.startup.tolevBack.simulations.application.dto.request.DebtPayoffSimulationRequest;
import com.br.startup.tolevBack.simulations.application.dto.response.DebtPayoffSimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SimulateDebtPayoffService {

    private final ProgressionIntegrationApi progressionIntegrationApi;

    public DebtPayoffSimulationResponse execute(DebtPayoffSimulationRequest request) {
        DebtProjectionResponse debt = progressionIntegrationApi.getDebtById(request.idDivida());

        BigDecimal valorDivida = debt.valorDivida() != null ? debt.valorDivida() : BigDecimal.ZERO;
        BigDecimal progresso = debt.progresso() != null ? debt.progresso() : BigDecimal.ZERO;
        BigDecimal valorPago = valorDivida
                .multiply(progresso)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal valorRestante = valorDivida.subtract(valorPago);

        BigDecimal pagamentoMensal = request.pagamentoMensal();
        int meses = pagamentoMensal.compareTo(BigDecimal.ZERO) <= 0 ? 0
                : valorRestante.divide(pagamentoMensal, 0, RoundingMode.CEILING).intValue();

        LocalDate dataPrevista = LocalDate.now().plusMonths(meses);

        return new DebtPayoffSimulationResponse(
                request.idDivida(),
                valorDivida,
                valorPago,
                valorRestante,
                pagamentoMensal,
                meses,
                dataPrevista
        );
    }
}
