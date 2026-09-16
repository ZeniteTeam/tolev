package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A janela do gráfico de gastos por categoria.
 *
 * <p>Escrito depois de um extrato de julho importado em setembro sumir da tela:
 * as 15 despesas estavam gravadas e categorizadas, e todo consumidor pedia
 * {@code meses=1} — uma janela que começa no dia 1º do mês corrente. Nada estava
 * quebrado e nada aparecia, que é o pior par possível.
 *
 * <p>Sem contexto Spring: o service só depende de {@link FinanceIntegrationApi},
 * e um stub que registra o período pedido é o suficiente para provar a conta.
 */
class GetSpendingByCategoryTest {

    private static final Long USUARIO = 1L;
    private static final Long BANCO = 6L;

    @Test
    void mesesUmPedeSomenteDoPrimeiroDiaDoMesCorrenteAteHoje() {
        var api = apiCom(List.of());

        new GetSpendingByCategory(api).execute(USUARIO, 1, null);

        assertThat(periodoPedido(api)).containsExactly(YearMonth.now().atDay(1), LocalDate.now());
    }

    @Test
    void mesesTresRecuaDoisMesesInteirosAlemDoCorrente() {
        var api = apiCom(List.of());

        new GetSpendingByCategory(api).execute(USUARIO, 3, null);

        assertThat(periodoPedido(api)[0]).isEqualTo(YearMonth.now().minusMonths(2).atDay(1));
    }

    /** O caso do extrato: janela que termina no passado, impossível de expressar em meses. */
    @Test
    void periodoExplicitoChegaIntactoAoFinance() {
        var api = apiCom(List.of());
        var inicio = LocalDate.of(2026, 7, 2);
        var fim = LocalDate.of(2026, 7, 30);

        new GetSpendingByCategory(api).execute(USUARIO, inicio, fim, null);

        assertThat(periodoPedido(api)).containsExactly(inicio, fim);
    }

    @Test
    void agrupaPorCategoriaSomandoSoAsDespesas() {
        var api = apiCom(List.of(
                despesa("60.00", "Alimentação", BANCO),
                despesa("40.00", "Alimentação", BANCO),
                despesa("100.00", "Saúde", BANCO),
                receita("500.00", "Salário", BANCO)));

        var resposta = new GetSpendingByCategory(api)
                .execute(USUARIO, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31), null);

        assertThat(resposta.totalDespesas()).isEqualByComparingTo("200.00");
        assertThat(resposta.totalTransacoes()).isEqualTo(3);
        // Ordenado do maior gasto para o menor, e a receita fica de fora.
        assertThat(resposta.pontos()).extracting(p -> p.nome())
                .containsExactly("Alimentação", "Saúde");
        assertThat(resposta.pontos().get(0).valor()).isEqualByComparingTo("100.00");
        assertThat(resposta.pontos().get(0).percentual()).isEqualByComparingTo("50.00");
    }

    @Test
    void filtroPorBancoDeixaDeForaOQueVeioDeOutraProcedencia() {
        var api = apiCom(List.of(
                despesa("60.00", "Alimentação", BANCO),
                despesa("100.00", "Saúde", 99L),
                despesa("30.00", "Lazer", null)));

        var resposta = new GetSpendingByCategory(api)
                .execute(USUARIO, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31), BANCO);

        assertThat(resposta.totalTransacoes()).isEqualTo(1);
        assertThat(resposta.totalDespesas()).isEqualByComparingTo("60.00");
    }

    @Test
    void janelaVaziaRespondeZeradaEmVezDeQuebrar() {
        var resposta = new GetSpendingByCategory(apiCom(List.of()))
                .execute(USUARIO, 1, null);

        assertThat(resposta.totalTransacoes()).isZero();
        assertThat(resposta.totalDespesas()).isEqualByComparingTo("0");
        assertThat(resposta.pontos()).isEmpty();
    }

    // ---------- apoio ----------

    private static TransactionResponse despesa(String valor, String categoria, Long idBanco) {
        return transacao(valor, categoria, idBanco, TipoTransacao.DESPESA);
    }

    private static TransactionResponse receita(String valor, String categoria, Long idBanco) {
        return transacao(valor, categoria, idBanco, TipoTransacao.RECEITA);
    }

    private static TransactionResponse transacao(
            String valor, String categoria, Long idBanco, TipoTransacao tipo) {
        return new TransactionResponse(
                1L, USUARIO, null, idBanco, null, null,
                new BigDecimal(valor), LocalDate.of(2026, 7, 15), tipo,
                categoria, categoria, false, null, null, null,
                7L, null, categoria, "#000000");
    }

    /**
     * Mock em vez de stub escrito à mão: {@link FinanceIntegrationApi} é a porta
     * de um módulo inteiro e cresce com ele, e um stub manual passaria a quebrar
     * este teste por métodos que ele não usa.
     */
    private static FinanceIntegrationApi apiCom(List<TransactionResponse> transacoes) {
        var api = mock(FinanceIntegrationApi.class);
        when(api.getTransactionsByUserAndPeriod(any(), any(), any())).thenReturn(transacoes);
        return api;
    }

    /** O período que o service pediu ao finance — [0] início, [1] fim. */
    private static LocalDate[] periodoPedido(FinanceIntegrationApi api) {
        var inicio = ArgumentCaptor.forClass(LocalDate.class);
        var fim = ArgumentCaptor.forClass(LocalDate.class);
        verify(api).getTransactionsByUserAndPeriod(eq(USUARIO), inicio.capture(), fim.capture());
        return new LocalDate[] {inicio.getValue(), fim.getValue()};
    }
}
