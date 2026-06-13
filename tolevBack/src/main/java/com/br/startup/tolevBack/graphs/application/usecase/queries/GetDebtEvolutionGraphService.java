package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.graphs.application.dto.response.DebtEvolutionGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.DebtEvolutionGraphResponse.DebtDataPoint;
import com.br.startup.tolevBack.progression.application.dto.response.DebtPayoffEstimateResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDebtEvolutionGraphService {

    private final ProgressionIntegrationApi progressionIntegrationApi;

    public DebtEvolutionGraphResponse execute(Long idUsuario) {
        DebtPayoffEstimateResponse estimate = progressionIntegrationApi.getDebtPayoffEstimate(idUsuario);
        List<DebtProjectionResponse> projections = progressionIntegrationApi.getDebtProjection(idUsuario);

        List<DebtDataPoint> pontos = projections.stream()
                .map(d -> new DebtDataPoint(
                        d.idDivida(),
                        d.valorDivida(),
                        d.progresso(),
                        d.status() != null ? d.status().name() : null
                ))
                .toList();

        return new DebtEvolutionGraphResponse(
                idUsuario,
                estimate.totalDividas(),
                estimate.totalPago(),
                estimate.totalRestante(),
                estimate.percentualPago(),
                pontos
        );
    }
}
