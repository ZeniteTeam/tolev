package com.br.startup.tolevBack.simulations.application.usecase.commands;

import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import com.br.startup.tolevBack.simulations.application.dto.request.MetaProjectionSimulationRequest;
import com.br.startup.tolevBack.simulations.application.dto.response.MetaProjectionSimulationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SimulateMetaProjectionService {

    private final ProgressionIntegrationApi progressionIntegrationApi;

    public MetaProjectionSimulationResponse execute(MetaProjectionSimulationRequest request) {
        MetaResponse meta = progressionIntegrationApi.getMetaById(request.idMeta());

        BigDecimal valorMeta = meta.valorMeta() != null ? meta.valorMeta() : BigDecimal.ZERO;
        BigDecimal progressoAtual = meta.progresso() != null ? meta.progresso() : BigDecimal.ZERO;
        BigDecimal valorAtingido = valorMeta
                .multiply(progressoAtual)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal valorRestante = valorMeta.subtract(valorAtingido);

        BigDecimal contribuicao = request.contribuicaoMensal();
        int meses = contribuicao.compareTo(BigDecimal.ZERO) <= 0 ? -1
                : valorRestante.divide(contribuicao, 0, RoundingMode.CEILING).intValue();

        LocalDate dataPrevista = meses > 0 ? LocalDate.now().plusMonths(meses) : null;

        return new MetaProjectionSimulationResponse(
                request.idMeta(),
                meta.nomeMeta(),
                valorMeta,
                progressoAtual,
                valorRestante,
                contribuicao,
                meses,
                dataPrevista
        );
    }
}
