package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import com.br.startup.tolevBack.users.internal.enums.MetodoQuitacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A projeção de quitação.
 *
 * <p>Os casos aqui são os que quebram a tela em vez de apenas errar um número:
 * usuário sem dívida (a maioria das contas novas), renda zero (o padrão quando
 * ninguém personalizou preferência), buraco no meio do cronograma e parcela
 * vencida — que é dinheiro devido agora, não um ponto no passado.
 *
 * <p>Sem contexto Spring: o service só fala com duas integration APIs.
 */
class GetDebtProjectionGraphServiceTest {

    private static final Long USUARIO = 1L;
    private static final YearMonth HOJE = YearMonth.now();

    @Test
    void semParcelaEmAbertoNaoHaDataParaPrever() {
        var service = service(List.of(), new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.dataPrevistaQuitacao()).isNull();
        assertThat(r.mesesRestantes()).isZero();
        assertThat(r.meses()).isEmpty();
        assertThat(r.curvaProgresso()).isEmpty();
    }

    @Test
    void dividaInteiramentePagaChegaACemPorCento() {
        var service = service(
                List.of(divida(parcela(1, "500", HOJE.minusMonths(1), StatusParcela.PAGA))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.percentualPago()).isEqualByComparingTo("100");
        assertThat(r.totalPago()).isEqualByComparingTo("500");
        assertThat(r.totalRestante()).isEqualByComparingTo("0");
    }

    /** O padrão de quem nunca abriu preferências é renda zero — e divisão por zero. */
    @Test
    void rendaZeroNaoEstouraNaDivisao() {
        var service = service(
                List.of(divida(parcela(1, "400", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                BigDecimal.ZERO);

        var r = service.execute(USUARIO);

        assertThat(r.comprometimentoRenda()).isEqualByComparingTo("0");
        assertThat(r.rendaMensal()).isEqualByComparingTo("0");
    }

    @Test
    void mesSemParcelaContinuaNaSerie() {
        var service = service(
                List.of(divida(
                        parcela(1, "300", HOJE.plusMonths(1), StatusParcela.PENDENTE),
                        parcela(2, "300", HOJE.plusMonths(3), StatusParcela.PENDENTE))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        // hoje, +1, +2 (vazio) e +3: pular o buraco encurtaria o eixo do tempo.
        assertThat(r.meses()).extracting("mes").containsExactly(
                HOJE.toString(),
                HOJE.plusMonths(1).toString(),
                HOJE.plusMonths(2).toString(),
                HOJE.plusMonths(3).toString());
        assertThat(r.meses().get(2).pagamentoPrevisto()).isEqualByComparingTo("0");
        assertThat(r.meses().get(2).dividaRestante()).isEqualByComparingTo("300");
        assertThat(r.mesesRestantes()).isEqualTo(4);
    }

    @Test
    void saldoDesceAteZeroNoMesDaQuitacao() {
        var service = service(
                List.of(divida(
                        parcela(1, "250", HOJE.plusMonths(1), StatusParcela.PENDENTE),
                        parcela(2, "250", HOJE.plusMonths(2), StatusParcela.PENDENTE))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.totalRestante()).isEqualByComparingTo("500");
        assertThat(r.meses().get(0).dividaRestante()).isEqualByComparingTo("500");
        assertThat(r.meses().get(2).dividaRestante()).isEqualByComparingTo("0");
        assertThat(r.curvaProgresso().get(r.curvaProgresso().size() - 1).percentualQuitado())
                .isEqualByComparingTo("100");
    }

    /** Parcela vencida é dinheiro devido agora: ela cai no balde do mês corrente. */
    @Test
    void parcelaAtrasadaEntraNoMesCorrente() {
        var service = service(
                List.of(divida(
                        parcela(1, "200", HOJE.minusMonths(2), StatusParcela.ATRASADA),
                        parcela(2, "200", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.meses().get(0).mes()).isEqualTo(HOJE.toString());
        assertThat(r.meses().get(0).pagamentoPrevisto()).isEqualByComparingTo("200");
    }

    /** Só atrasadas: a quitação não pode ser prevista para uma data já vencida. */
    @Test
    void tudoAtrasadoPreveQuitacaoNoMesCorrente() {
        var service = service(
                List.of(divida(parcela(1, "150", HOJE.minusMonths(3), StatusParcela.ATRASADA))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.dataPrevistaQuitacao()).isEqualTo(HOJE.atEndOfMonth());
        assertThat(r.mesesRestantes()).isEqualTo(1);
    }

    /**
     * O mês corrente pode já ter sido pago; o compromisso mensal honesto é o do
     * próximo mês inteiro.
     */
    @Test
    void pagamentoMensalPrevistoOlhaOProximoMesInteiro() {
        var service = service(
                List.of(divida(
                        parcela(1, "100", HOJE, StatusParcela.PENDENTE),
                        parcela(2, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("3000"));

        var r = service.execute(USUARIO);

        assertThat(r.pagamentoMensalPrevisto()).isEqualByComparingTo("600");
        assertThat(r.comprometimentoRenda()).isEqualByComparingTo("20.00");
        assertThat(r.rendaMensal()).isEqualByComparingTo("3000");
    }

    /** A sobra só existe depois que os gastos fixos saem da renda. */
    @Test
    void sobraDescontaGastosFixosEParcelas() {
        var service = service(
                List.of(divida(parcela(1, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("3000"),
                List.of(gastoFixo("Aluguel", "1200"), gastoFixo("Internet", "120")));

        var r = service.execute(USUARIO);

        assertThat(r.gastosFixosMensais()).isEqualByComparingTo("1320");
        assertThat(r.sobraMensal()).isEqualByComparingTo("1080");
    }

    /**
     * Na 50/30/20 a fatia de lazer é um teto: sobra R$ 1.200, mas o plano só
     * autoriza gastar os 20% da renda.
     */
    @Test
    void regra503020LimitaAoTetoDeLazerDoPlano() {
        var service = service(
                List.of(divida(parcela(1, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.REGRA_50_30_20, 50, 30, 20),
                List.of(gastoFixo("Aluguel", "1200")));

        var r = service.execute(USUARIO);

        assertThat(r.sobraMensal()).isEqualByComparingTo("1200");
        assertThat(r.limiteLazerMensal()).isEqualByComparingTo("600");
        assertThat(r.disponivelPorDia()).isEqualByComparingTo(porDia("600"));
    }

    /** Plano personalizado: 40% de lazer aumenta o teto junto. */
    @Test
    void tetoDeLazerSegueOPercentualQueOUsuarioEscolheu() {
        var service = service(
                List.of(divida(parcela(1, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.REGRA_50_30_20, 30, 30, 40),
                List.of(gastoFixo("Aluguel", "1200")));

        var r = service.execute(USUARIO);

        assertThat(r.limiteLazerMensal()).isEqualByComparingTo("1200");
        assertThat(r.disponivelPorDia()).isEqualByComparingTo(porDia("1200"));
    }

    /**
     * No base zero não há fatia pré-definida: o que sobra depois dos fixos e
     * das parcelas é justamente o que está para ser distribuído.
     */
    @Test
    void baseZeroNaoImpoeTetoEUsaASobraInteira() {
        var service = service(
                List.of(divida(parcela(1, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.BASE_ZERO, 50, 30, 20),
                List.of(gastoFixo("Aluguel", "1200")));

        var r = service.execute(USUARIO);

        assertThat(r.limiteLazerMensal()).isNull();
        assertThat(r.disponivelPorDia()).isEqualByComparingTo(porDia("1200"));
    }

    /** A realidade é o teto mais duro: plano nenhum autoriza gastar o que não há. */
    @Test
    void sobraApertadaGanhaDoTetoDoPlano() {
        var service = service(
                List.of(divida(parcela(1, "900", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.REGRA_50_30_20, 50, 30, 20),
                List.of(gastoFixo("Aluguel", "1800")));

        var r = service.execute(USUARIO);

        // Plano autorizaria 600; sobraram 300.
        assertThat(r.limiteLazerMensal()).isEqualByComparingTo("600");
        assertThat(r.sobraMensal()).isEqualByComparingTo("300");
        assertThat(r.disponivelPorDia()).isEqualByComparingTo(porDia("300"));
    }

    @Test
    void comprometimentoDentroDoPlanoEhSaudavel() {
        var service = service(
                List.of(divida(parcela(1, "600", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("3000"));

        var r = service.execute(USUARIO);

        // 20% da renda, com plano reservando 30%.
        assertThat(r.nivelComprometimento()).isEqualTo("SAUDAVEL");
        assertThat(r.limiteDividasPlano()).isEqualByComparingTo("30");
    }

    @Test
    void acimaDoPlanoMasAbaixoDeMetadeEhAtencao() {
        var service = service(
                List.of(divida(parcela(1, "1200", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("3000"));

        var r = service.execute(USUARIO);

        assertThat(r.comprometimentoRenda()).isEqualByComparingTo("40.00");
        assertThat(r.nivelComprometimento()).isEqualTo("ATENCAO");
    }

    /** A régua é a do usuário: quem planejou 45% ainda está dentro com 40%. */
    @Test
    void planoMaisFolgadoMudaAReguaDoAviso() {
        var service = service(
                List.of(divida(parcela(1, "1200", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.REGRA_50_30_20, 35, 45, 20),
                List.of());

        var r = service.execute(USUARIO);

        assertThat(r.nivelComprometimento()).isEqualTo("SAUDAVEL");
    }

    /** Metade da renda em dívida é crítico mesmo com plano permissivo. */
    @Test
    void acimaDeMetadeDaRendaEhCriticoAindaQueOPlanoPermita() {
        var service = service(
                List.of(divida(parcela(1, "1800", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                preferencias(new BigDecimal("3000"), MetodoOrcamento.REGRA_50_30_20, 20, 60, 20),
                List.of());

        var r = service.execute(USUARIO);

        assertThat(r.comprometimentoRenda()).isEqualByComparingTo("60.00");
        assertThat(r.nivelComprometimento()).isEqualTo("CRITICO");
    }

    @Test
    void semRendaNaoHaProporcaoParaAvaliar() {
        var service = service(
                List.of(divida(parcela(1, "400", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                BigDecimal.ZERO);

        var r = service.execute(USUARIO);

        assertThat(r.nivelComprometimento()).isEqualTo("SEM_RENDA");
        assertThat(r.limiteLazerMensal()).isNull();
    }

    @Test
    void semDividaOComprometimentoEhSaudavel() {
        var service = service(List.of(), new BigDecimal("3000"));

        var r = service.execute(USUARIO);

        assertThat(r.nivelComprometimento()).isEqualTo("SAUDAVEL");
    }

    /** Fixos + parcela passando da renda: a falta é o que a tela precisa dizer. */
    @Test
    void sobraNegativaQuandoOsCompromissosPassamDaRenda() {
        var service = service(
                List.of(divida(parcela(1, "900", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("2000"),
                List.of(gastoFixo("Aluguel", "1500")));

        var r = service.execute(USUARIO);

        assertThat(r.sobraMensal()).isEqualByComparingTo("-400");
        assertThat(r.disponivelPorDia().signum()).isNegative();
    }

    /**
     * Sem renda não há sobra nem falta, só ausência de dado. Zero leria como
     * "não sobra nada", que é uma afirmação que o app não pode fazer.
     */
    @Test
    void semRendaASobraEhNulaEmVezDeZero() {
        var service = service(
                List.of(divida(parcela(1, "400", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                BigDecimal.ZERO,
                List.of(gastoFixo("Aluguel", "1200")));

        var r = service.execute(USUARIO);

        assertThat(r.sobraMensal()).isNull();
        assertThat(r.disponivelPorDia()).isNull();
        assertThat(r.gastosFixosMensais()).isEqualByComparingTo("1200");
    }

    /** Sem dívida a conta continua: é renda menos os fixos. */
    @Test
    void semDividaASobraAindaDescontaOsFixos() {
        var service = service(List.of(), new BigDecimal("3000"), List.of(gastoFixo("Aluguel", "1200")));

        var r = service.execute(USUARIO);

        assertThat(r.dataPrevistaQuitacao()).isNull();
        assertThat(r.sobraMensal()).isEqualByComparingTo("1800");
    }

    @Test
    void parcelaCanceladaNaoContaEmLugarNenhum() {
        var service = service(
                List.of(divida(
                        parcela(1, "500", HOJE.plusMonths(1), StatusParcela.CANCELADA),
                        parcela(2, "500", HOJE.plusMonths(2), StatusParcela.PENDENTE))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        assertThat(r.totalRestante()).isEqualByComparingTo("500");
        assertThat(r.mesesRestantes()).isEqualTo(3);
    }

    @Test
    void jurosRestantesSomamApenasAsParcelasEmAberto() {
        var service = service(
                List.of(divida(
                        parcela(1, "500", HOJE.minusMonths(1), StatusParcela.PAGA),
                        parcela(2, "500", HOJE.plusMonths(1), StatusParcela.PENDENTE))),
                new BigDecimal("5000"));

        var r = service.execute(USUARIO);

        // Cada parcela leva 50 de juros; só a aberta entra.
        assertThat(r.totalJurosRestante()).isEqualByComparingTo("50");
        assertThat(r.percentualPago()).isEqualByComparingTo("50.00");
    }

    // --- montagem ---------------------------------------------------------

    private GetDebtProjectionGraphService service(List<DividaResponse> dividas, BigDecimal renda) {
        return service(dividas, renda, List.of());
    }

    private GetDebtProjectionGraphService service(
            List<DividaResponse> dividas, BigDecimal renda, List<GastoFixoResponse> gastosFixos) {
        return service(dividas, preferencias(renda), gastosFixos);
    }

    private GetDebtProjectionGraphService service(
            List<DividaResponse> dividas,
            PreferenciaFinanceiraResponse prefs,
            List<GastoFixoResponse> gastosFixos) {
        var progression = mock(ProgressionIntegrationApi.class);
        when(progression.getDividasByUser(any())).thenReturn(dividas);

        var users = mock(UserIntegrationApi.class);
        when(users.getPreferencias(any())).thenReturn(prefs);

        var finance = mock(FinanceIntegrationApi.class);
        when(finance.getGastosFixos(any())).thenReturn(gastosFixos);

        return new GetDebtProjectionGraphService(progression, users, finance);
    }

    /** O valor mensal esperado, espalhado pelos dias deste mês. */
    private BigDecimal porDia(String mensal) {
        return new BigDecimal(mensal)
                .divide(new BigDecimal(HOJE.lengthOfMonth()), 2, java.math.RoundingMode.HALF_UP);
    }

    private GastoFixoResponse gastoFixo(String nome, String valor) {
        return new GastoFixoResponse(1L, USUARIO, nome, new BigDecimal(valor), 10, LocalDate.now());
    }

    private PreferenciaFinanceiraResponse preferencias(BigDecimal renda) {
        return preferencias(renda, MetodoOrcamento.REGRA_50_30_20, 50, 30, 20);
    }

    private PreferenciaFinanceiraResponse preferencias(
            BigDecimal renda, MetodoOrcamento metodo, int fixos, int dividas, int lazer) {
        return new PreferenciaFinanceiraResponse(
                USUARIO,
                MetodoQuitacao.AVALANCHE,
                BigDecimal.ZERO,
                metodo,
                renda,
                fixos, dividas, lazer,
                BigDecimal.ZERO);
    }

    private DividaResponse divida(ParcelaResponse... parcelas) {
        List<ParcelaResponse> lista = Arrays.asList(parcelas);
        return new DividaResponse(
                10L,
                USUARIO,
                "Cartão",
                "Nubank",
                TipoDivida.CARTAO,
                StatusDivida.ATIVA,
                new BigDecimal("1000"),
                new BigDecimal("2"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("500"),
                3,
                lista.size(),
                LocalDate.now(),
                LocalDate.now(),
                SistemaAmortizacao.PRICE,
                RegimeJuros.COMPOSTO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                lista);
    }

    /** Juros de 10% do valor, só para o total de juros ter de onde sair. */
    private ParcelaResponse parcela(int numero, String valor, YearMonth mes, StatusParcela status) {
        BigDecimal total = new BigDecimal(valor);
        BigDecimal juros = total.divide(BigDecimal.TEN);
        return new ParcelaResponse(
                (long) numero,
                numero,
                total,
                total.subtract(juros),
                juros,
                status,
                mes.atDay(10),
                status == StatusParcela.PAGA ? mes.atDay(10) : null);
    }
}
