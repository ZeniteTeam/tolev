package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisImpactResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisRiskResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisScorePointResponse;
import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisVariableResponse;
import com.br.startup.tolevBack.analysis.integration.api.AnalysisIntegrationApi;
import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreDriversGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreDriversGraphResponse.Driver;
import com.br.startup.tolevBack.graphs.application.dto.response.ScoreEvolutionGraphResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Os três defeitos corrigidos em score-drivers e score-evolution.
 *
 * <p>Nenhum contexto Spring: os dois services dependem só de
 * {@link AnalysisIntegrationApi}, então um stub basta e o teste roda em ms.
 */
class ScoreGraphServicesTest {

    private static final Long USUARIO = 1L;

    // ---------- score-drivers: escala e contribuicaoMaxima ----------

    @Test
    void emSaudeFinanceiraAContribuicaoSaiEmPontosEOMaximoExiste() {
        var service = new GetScoreDriversGraphService(stub(List.of(
                variavel("COMPROMETIMENTO_RENDA", "72", "80.00", "0.60", "48.00"),
                variavel("MESES_DE_RESERVA", "1,5", "50.00", "0.40", "20.00"))));

        ScoreDriversGraphResponse r = service.execute(USUARIO, TipoAnalise.SAUDE_FINANCEIRA);

        assertThat(r.drivers()).extracting(Driver::escala).containsOnly("PONTOS");
        assertThat(r.drivers()).extracting(Driver::contribuicaoMaxima)
                .containsExactly(new BigDecimal("60.00"), new BigDecimal("40.00"));
        assertThat(r.scoreTotal()).isEqualByComparingTo("68.00");
        assertThat(r.pontosPerdidos()).isEqualByComparingTo("32.00");
    }

    @Test
    void emInadimplenciaOPesoEOCoeficienteDoLogitoEntaoNaoHaMaximo() {
        // peso 2,40 é o b do modelo: peso × 100 daria "240 pontos", que não existe.
        var service = new GetScoreDriversGraphService(stub(List.of(
                variavel("ATRASOS_RECENTES", "2 parcelas", "100.00", "2.40", "1.20"))));

        ScoreDriversGraphResponse r = service.execute(USUARIO, TipoAnalise.INADIMPLENCIA);

        assertThat(r.drivers()).singleElement().satisfies(d -> {
            assertThat(d.escala()).isEqualTo("LOGITO");
            assertThat(d.contribuicaoMaxima()).isNull();
        });
        assertThat(r.pontosPerdidos()).isNull();
    }

    @Test
    void emPrevisaoAEscalaEReaisMasMesesAteQuitacaoEEmMeses() {
        var service = new GetScoreDriversGraphService(stub(List.of(
                variavel("SALDO_ATUAL", "R$ 1.500,00", "1500.00", "1", "1500.00"),
                variavel("MESES_ATE_QUITACAO", "7 meses", "7", "1", "7"))));

        ScoreDriversGraphResponse r = service.execute(USUARIO, TipoAnalise.PREVISAO);

        assertThat(r.drivers()).extracting(Driver::escala).containsExactly("REAIS", "MESES");
        assertThat(r.drivers()).extracting(Driver::contribuicaoMaxima).containsOnlyNulls();
        assertThat(r.pontosPerdidos()).isNull();
    }

    // ---------- score-drivers: renormalização do RISCO ----------

    @Test
    void emRiscoOScoreERenormalizadoPorSomaDePesos() {
        // Só duas das quatro análises rodaram: Σ peso = 0,70, não 1,00.
        // Sem renormalizar, o total sairia 39,00 — sistematicamente baixo, e
        // discordando do que score-evolution devolve no mesmo dia.
        var service = new GetScoreDriversGraphService(stub(List.of(
                variavel("RISCO_SAUDE_FINANCEIRA", "ATENCAO", "60.00", "0.40", "24.00"),
                variavel("RISCO_CONSUMO", "BOA", "50.00", "0.30", "15.00"))));

        ScoreDriversGraphResponse r = service.execute(USUARIO, TipoAnalise.RISCO);

        assertThat(r.scoreTotal()).isEqualByComparingTo("55.71");
        assertThat(r.pontosPerdidos()).isEqualByComparingTo("44.29");
        // Renormalizado, score e perdidos fecham em 100.
        assertThat(r.scoreTotal().add(r.pontosPerdidos())).isEqualByComparingTo("100.00");
    }

    // ---------- score-evolution: tendência do RISCO ----------

    @Test
    void riscoSubindoEPiora() {
        var service = new GetScoreEvolutionGraphService(
                stubHistorico(List.of(ponto("2026-08-01", "40"), ponto("2026-08-30", "55"))));

        ScoreEvolutionGraphResponse r = service.execute(USUARIO, TipoAnalise.RISCO);

        assertThat(r.tendencia()).isEqualTo("PIORANDO");
        // A variação continua no sinal cru do score: quem interpreta é a tendência.
        assertThat(r.variacao()).isEqualByComparingTo("15");
    }

    @Test
    void riscoCaindoEMelhora() {
        var service = new GetScoreEvolutionGraphService(
                stubHistorico(List.of(ponto("2026-08-01", "55"), ponto("2026-08-30", "40"))));

        assertThat(service.execute(USUARIO, TipoAnalise.RISCO).tendencia()).isEqualTo("MELHORANDO");
    }

    @Test
    void nosDemaisTiposScoreSubindoContinuaSendoMelhora() {
        var service = new GetScoreEvolutionGraphService(
                stubHistorico(List.of(ponto("2026-08-01", "60"), ponto("2026-08-30", "75"))));

        assertThat(service.execute(USUARIO, TipoAnalise.SAUDE_FINANCEIRA).tendencia())
                .isEqualTo("MELHORANDO");
    }

    @Test
    void umPontoSoEPrimeiraMedicao() {
        var service = new GetScoreEvolutionGraphService(
                stubHistorico(List.of(ponto("2026-08-30", "55"))));

        assertThat(service.execute(USUARIO, TipoAnalise.RISCO).tendencia())
                .isEqualTo("PRIMEIRA_MEDICAO");
    }

    // ---------- fixtures ----------

    private AnalysisVariableResponse variavel(
            String nome, String valor, String valorFaixa, String peso, String coeficiente) {
        return new AnalysisVariableResponse(
                nome, valor, new BigDecimal(valorFaixa), new BigDecimal(peso),
                new BigDecimal(coeficiente), "NEUTRO", "referência");
    }

    private AnalysisScorePointResponse ponto(String data, String score) {
        return new AnalysisScorePointResponse(
                LocalDate.parse(data), TipoAnalise.RISCO, new BigDecimal(score),
                "ATENCAO", NivelRisco.MEDIO, null);
    }

    private AnalysisIntegrationApi stub(List<AnalysisVariableResponse> variaveis) {
        return new StubAnalysisApi(variaveis, List.of());
    }

    private AnalysisIntegrationApi stubHistorico(List<AnalysisScorePointResponse> historico) {
        return new StubAnalysisApi(List.of(), historico);
    }

    /** Devolve o que o teste plantou; o resto não é usado por estes dois services. */
    private record StubAnalysisApi(
            List<AnalysisVariableResponse> variaveis,
            List<AnalysisScorePointResponse> historico
    ) implements AnalysisIntegrationApi {

        @Override
        public List<AnalysisResponse> getAnalysisByUser(Long idUsuario) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<AnalysisScorePointResponse> getScoreHistory(Long idUsuario, TipoAnalise tipo) {
            return historico;
        }

        @Override
        public List<AnalysisVariableResponse> getLatestVariables(Long idUsuario, TipoAnalise tipo) {
            return variaveis;
        }

        @Override
        public List<AnalysisImpactResponse> getImpacts(Long idUsuario) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<AnalysisRiskResponse> getLatestRisk(Long idUsuario) {
            throw new UnsupportedOperationException();
        }
    }
}
