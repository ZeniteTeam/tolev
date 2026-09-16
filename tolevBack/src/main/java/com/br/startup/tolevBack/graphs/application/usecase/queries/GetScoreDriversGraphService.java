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
 *
 * <p>O que cada analisador grava em {@code coeficiente} não é a mesma coisa em
 * todo tipo de análise — daí o campo {@code escala} de cada driver. Em
 * SAUDE_FINANCEIRA, CONSUMO e RISCO o peso é uma fração e o coeficiente sai em
 * pontos de uma nota 0–100; em INADIMPLENCIA o peso é o coeficiente b do logito
 * (2,40, por exemplo) e a contribuição sai em logitos; em PREVISAO o peso é
 * sempre 1 e a contribuição é um valor em reais — ou meses, no caso de
 * MESES_ATE_QUITACAO. Multiplicar peso por 100 só significa alguma coisa no
 * primeiro grupo.
 */
@Service
@RequiredArgsConstructor
public class GetScoreDriversGraphService {

    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("100");

    /** Contribuição em pontos de uma nota 0–100: a única escala com máximo. */
    private static final String ESCALA_PONTOS = "PONTOS";
    private static final String ESCALA_LOGITO = "LOGITO";
    private static final String ESCALA_REAIS = "REAIS";
    private static final String ESCALA_MESES = "MESES";

    private static final String MESES_ATE_QUITACAO = "MESES_ATE_QUITACAO";

    private final AnalysisIntegrationApi analysisApi;

    public ScoreDriversGraphResponse execute(Long idUsuario, TipoAnalise tipo) {
        List<AnalysisVariableResponse> variaveis = analysisApi.getLatestVariables(idUsuario, tipo);

        if (variaveis.isEmpty()) {
            return new ScoreDriversGraphResponse(
                    idUsuario, tipo.name(), BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        List<Driver> drivers = variaveis.stream().map(v -> paraDriver(v, tipo)).toList();

        BigDecimal total = drivers.stream()
                .map(Driver::contribuicao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // RISCO renormaliza por Σ peso, como RiscoConsolidator faz: quando uma
        // das análises não roda (usuário sem dívida não gera INADIMPLENCIA), os
        // pesos restantes não somam 1 e o score sairia sistematicamente baixo —
        // e discordando do que score-evolution devolve para o mesmo dia.
        BigDecimal divisor = tipo == TipoAnalise.RISCO ? somaPesos(drivers) : BigDecimal.ONE;

        return new ScoreDriversGraphResponse(
                idUsuario,
                tipo.name(),
                dividir(total, divisor),
                pontosPerdidos(tipo, drivers, total, divisor),
                drivers);
    }

    /**
     * Quanto ainda cabia somar. Só existe em PONTOS: somar os "máximos" de uma
     * escala em logitos daria 240 e de uma em reais daria 100 sempre.
     */
    private BigDecimal pontosPerdidos(
            TipoAnalise tipo, List<Driver> drivers, BigDecimal total, BigDecimal divisor) {
        if (!emPontos(tipo)) {
            return null;
        }
        BigDecimal maximo = drivers.stream()
                .map(Driver::contribuicaoMaxima)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return dividir(maximo.subtract(total).max(BigDecimal.ZERO), divisor);
    }

    private Driver paraDriver(AnalysisVariableResponse v, TipoAnalise tipo) {
        BigDecimal peso = v.peso() != null ? v.peso() : BigDecimal.ZERO;
        return new Driver(
                v.nome(),
                v.valor(),
                v.valorFaixa(),
                peso,
                v.coeficiente() != null ? v.coeficiente() : BigDecimal.ZERO,
                emPontos(tipo)
                        ? peso.multiply(NOTA_MAXIMA).setScale(2, RoundingMode.HALF_UP)
                        : null,
                escala(tipo, v.nome()),
                v.impactoResultado(),
                v.faixaReferencia());
    }

    private String escala(TipoAnalise tipo, String nomeVariavel) {
        return switch (tipo) {
            case INADIMPLENCIA -> ESCALA_LOGITO;
            case PREVISAO -> MESES_ATE_QUITACAO.equals(nomeVariavel) ? ESCALA_MESES : ESCALA_REAIS;
            case SAUDE_FINANCEIRA, CONSUMO, RISCO -> ESCALA_PONTOS;
        };
    }

    private boolean emPontos(TipoAnalise tipo) {
        return ESCALA_PONTOS.equals(escala(tipo, null));
    }

    private BigDecimal somaPesos(List<Driver> drivers) {
        return drivers.stream().map(Driver::peso).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Divisor zero devolve zero em vez de estourar, como {@code Calculo.dividir}. */
    private BigDecimal dividir(BigDecimal valor, BigDecimal divisor) {
        if (divisor.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return valor.divide(divisor, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
}
