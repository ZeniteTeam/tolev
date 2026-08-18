package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisVariableResponse;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreDriversGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreDriversGraphResponse.Driver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Decomposição da nota atual: o que cada indicador somou e o que deixou de somar.
 *
 * <p>Sai direto de {@code tb_analise_resultado_variavel}, que guarda peso e
 * coeficiente de cada variável justamente para o resultado ser explicável em vez
 * de um número solto.
 */
@Service
@RequiredArgsConstructor
public class GetScoreDriversGraphService {

    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("100");

    private final AnalysisIntegrationApi analysisApi;

    public ScoreDriversGraphResponse execute(Long idUsuario, TipoAnalise tipo) {
        List<AnalysisVariableResponse> variaveis = analysisApi.getLatestVariables(idUsuario, tipo);

        if (variaveis.isEmpty()) {
            return new ScoreDriversGraphResponse(
                    idUsuario, tipo.name(), BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        List<Driver> drivers = variaveis.stream().map(this::paraDriver).toList();

        BigDecimal total = drivers.stream()
                .map(Driver::contribuicao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal maximo = drivers.stream()
                .map(Driver::contribuicaoMaxima)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ScoreDriversGraphResponse(
                idUsuario,
                tipo.name(),
                total.setScale(2, RoundingMode.HALF_UP),
                maximo.subtract(total).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP),
                drivers);
    }

    private Driver paraDriver(AnalysisVariableResponse v) {
        BigDecimal peso = v.peso() != null ? v.peso() : BigDecimal.ZERO;
        return new Driver(
                v.nome(),
                v.valor(),
                v.valorFaixa(),
                peso,
                v.coeficiente() != null ? v.coeficiente() : BigDecimal.ZERO,
                peso.multiply(NOTA_MAXIMA).setScale(2, RoundingMode.HALF_UP),
                v.impactoResultado(),
                v.faixaReferencia());
    }
}
