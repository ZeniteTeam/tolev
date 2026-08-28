package com.br.startup.tolevBack.progression.application.service;

import com.br.startup.tolevBack.progression.application.service.AmortizacaoService.ParcelaCalculada;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizacaoServiceTest {

    private final AmortizacaoService service = new AmortizacaoService();

    private static final LocalDate PRIMEIRO_VENCIMENTO = LocalDate.of(2026, 3, 10);

    private BigDecimal somaAmortizacao(List<ParcelaCalculada> tabela) {
        return tabela.stream().map(ParcelaCalculada::amortizacao).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    void priceCompostoMantemParcelaFixaEQuitaOPrincipal() {
        // R$ 1.000 em 12x a 2% a.m. → PMT tabelado de R$ 94,56.
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("1000.00"), 12, new BigDecimal("2"),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO);

        assertThat(tabela).hasSize(12);
        assertThat(tabela.get(0).valorTotal()).isEqualByComparingTo("94.56");
        // Todas as parcelas menos a última (que fecha o arredondamento) são iguais.
        assertThat(tabela.subList(0, 11))
                .allSatisfy(p -> assertThat(p.valorTotal()).isEqualByComparingTo("94.56"));
        assertThat(tabela.get(11).valorTotal()).isBetween(new BigDecimal("94.00"), new BigDecimal("95.00"));
        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("1000.00");
    }

    @Test
    void sacCompostoTemAmortizacaoFixaEParcelaDecrescente() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("1200.00"), 12, new BigDecimal("1"),
                SistemaAmortizacao.SAC, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO);

        assertThat(tabela).allSatisfy(p -> assertThat(p.amortizacao()).isEqualByComparingTo("100.00"));
        // 1ª: 100 + 1% de 1200 = 112,00 · última: 100 + 1% de 100 = 101,00
        assertThat(tabela.get(0).valorTotal()).isEqualByComparingTo("112.00");
        assertThat(tabela.get(11).valorTotal()).isEqualByComparingTo("101.00");
        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("1200.00");
    }

    @Test
    void jurosSimplesCobraSempreOMesmoValorSobreOPrincipal() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("1000.00"), 10, new BigDecimal("3"),
                SistemaAmortizacao.PRICE, RegimeJuros.SIMPLES, null, PRIMEIRO_VENCIMENTO);

        // 3% de 1.000 = 30,00 em toda parcela; amortização 1.000 / 10 = 100,00.
        assertThat(tabela).allSatisfy(p -> {
            assertThat(p.juros()).isEqualByComparingTo("30.00");
            assertThat(p.valorTotal()).isEqualByComparingTo("130.00");
        });
        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("1000.00");
    }

    @Test
    void compostoCustaMaisQueSimplesNoPrice() {
        BigDecimal pv = new BigDecimal("5000.00");
        BigDecimal taxa = new BigDecimal("4");

        BigDecimal jurosComposto = totalJuros(service.calcular(
                pv, 24, taxa, SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO));
        BigDecimal jurosSimples = totalJuros(service.calcular(
                pv, 24, taxa, SistemaAmortizacao.PRICE, RegimeJuros.SIMPLES, null, PRIMEIRO_VENCIMENTO));

        assertThat(jurosComposto).isLessThan(jurosSimples);
    }

    @Test
    void semJurosAsParcelasSaoOPrincipalDividido() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("300.00"), 3, BigDecimal.ZERO,
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO);

        assertThat(tabela).allSatisfy(p -> {
            assertThat(p.valorTotal()).isEqualByComparingTo("100.00");
            assertThat(p.juros()).isEqualByComparingTo("0.00");
        });
    }

    @Test
    void arredondamentoNaoPerdeCentavosComValorQueNaoDivide() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("100.00"), 3, BigDecimal.ZERO,
                SistemaAmortizacao.SAC, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO);

        // 33,33 + 33,33 + 33,34 — a última parcela absorve o resto.
        assertThat(tabela.get(2).valorTotal()).isEqualByComparingTo("33.34");
        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("100.00");
    }

    @Test
    void vencimentosCaemMesAMesAPartirDaDataInformada() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("600.00"), 3, new BigDecimal("1"),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO);

        assertThat(tabela).extracting(ParcelaCalculada::vencimento).containsExactly(
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 5, 10));
    }

    @Test
    void semParcelasOuSemValorNaoGeraTabela() {
        assertThat(service.calcular(new BigDecimal("500.00"), 0, BigDecimal.ONE,
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO)).isEmpty();
        assertThat(service.calcular(BigDecimal.ZERO, 12, BigDecimal.ONE,
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO, null, PRIMEIRO_VENCIMENTO)).isEmpty();
    }

    // ---- Ajuste de primeiro período: PV_adj = PV₀ · (1+i)^((d−30)/30) ----

    /** Devolve a parcela fixa do PRICE liberando o valor {@code dias} antes do 1º vencimento. */
    private BigDecimal pmtComCarencia(long dias) {
        return service.calcular(
                new BigDecimal("1000.00"), 12, new BigDecimal("2"),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO,
                PRIMEIRO_VENCIMENTO.minusDays(dias), PRIMEIRO_VENCIMENTO
        ).get(0).valorTotal();
    }

    @Test
    void carenciaDeExatos30DiasNaoMudaAParcela() {
        // d = 30 → expoente zero → fator 1 → mesma tabela do caso sem data.
        assertThat(pmtComCarencia(30)).isEqualByComparingTo("94.56");
    }

    @Test
    void primeiroPeriodoMaisLongoEncareceAParcela() {
        // d = 45 → PV·1,02^0,5 ≈ 1.009,95 → PMT ≈ 95,50
        BigDecimal pmt = pmtComCarencia(45);
        assertThat(pmt).isGreaterThan(new BigDecimal("94.56"));
        assertThat(pmt).isEqualByComparingTo("95.50");
    }

    @Test
    void primeiroPeriodoMaisCurtoBarateiaAParcela() {
        // d = 15 → expoente negativo → PV desconta e a parcela cai.
        assertThat(pmtComCarencia(15)).isLessThan(new BigDecimal("94.56"));
    }

    @Test
    void carenciaAbsurdaEIgnoradaEmVezDeExplodirAParcela() {
        // Liberação em 2022 com 1º vencimento em 2026: data errada, não carência.
        // Capitalizar 4 anos a 22% a.m. estouraria o double — o ajuste é descartado.
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("223.23"), 12, new BigDecimal("22.22"),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO,
                LocalDate.of(2022, 12, 12), PRIMEIRO_VENCIMENTO);

        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("223.23");
        assertThat(tabela.get(0).valorTotal()).isLessThan(new BigDecimal("100.00"));
    }

    @Test
    void liberacaoDepoisDoVencimentoNaoAjusta() {
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("1000.00"), 12, new BigDecimal("2"),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO,
                PRIMEIRO_VENCIMENTO.plusDays(5), PRIMEIRO_VENCIMENTO);

        assertThat(tabela.get(0).valorTotal()).isEqualByComparingTo("94.56");
    }

    @Test
    void nosJurosSimplesOAjusteEProporcionalENaoCapitaliza() {
        // d = 60 → expoente 1 → PV · (1 + 0,03) = 1.030,00 → amortização 103,00
        List<ParcelaCalculada> tabela = service.calcular(
                new BigDecimal("1000.00"), 10, new BigDecimal("3"),
                SistemaAmortizacao.PRICE, RegimeJuros.SIMPLES,
                PRIMEIRO_VENCIMENTO.minusDays(60), PRIMEIRO_VENCIMENTO);

        assertThat(somaAmortizacao(tabela)).isEqualByComparingTo("1030.00");
        assertThat(tabela.get(0).juros()).isEqualByComparingTo("30.90");
    }

    private BigDecimal totalJuros(List<ParcelaCalculada> tabela) {
        return tabela.stream().map(ParcelaCalculada::juros).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
