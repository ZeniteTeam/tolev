package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisImpactResponse;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.graphs.application.dto.response.ImpactRankingGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.ImpactRankingGraphResponse.ImpactoPonto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Ranking em reais: quanto cada problema detectado custa por ano.
 *
 * <p>Traduz a análise para a única unidade que todo mundo entende sem
 * explicação. É o gráfico que responde "onde está indo meu dinheiro" com nome
 * e valor, não com nota.
 */
@Service
@RequiredArgsConstructor
public class GetImpactRankingGraphService {

    private final AnalysisIntegrationApi analysisApi;

    public ImpactRankingGraphResponse execute(Long idUsuario) {
        List<AnalysisImpactResponse> impactos = analysisApi.getImpacts(idUsuario);

        BigDecimal custoAnualTotal = somar(impactos, AnalysisImpactResponse::impactoAnual);
        BigDecimal custoMensalTotal = somar(impactos, AnalysisImpactResponse::impactoMensal);

        List<ImpactoPonto> pontos = impactos.stream()
                .map(i -> new ImpactoPonto(
                        i.regra(),
                        i.descricao(),
                        i.entidadeTipo(),
                        i.entidadeId(),
                        i.gravidade(),
                        nz(i.impactoMensal()),
                        nz(i.impactoAnual()),
                        fatia(i.impactoAnual(), custoAnualTotal)))
                .toList();

        return new ImpactRankingGraphResponse(
                idUsuario,
                custoAnualTotal,
                custoMensalTotal,
                pontos);
    }

    private BigDecimal somar(
            List<AnalysisImpactResponse> impactos,
            java.util.function.Function<AnalysisImpactResponse, BigDecimal> campo) {
        return impactos.stream()
                .map(campo)
                .map(this::nz)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal fatia(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return nz(parte)
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
