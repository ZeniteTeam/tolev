package com.br.startup.tolevBack.progression.internal.mapper;

import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O custo dos juros de uma dívida.
 *
 * <p>Escrito depois de uma dívida quitada aparecer na tela com "custou no total
 * R$ 1.200" e "R$ 1.200 disso foram juros" — os dois números idênticos. A causa
 * era {@code valorDivida} fazendo dois trabalhos: ele nasce como o principal
 * contratado e depois é decrementado a cada amortização, até ser zerado na
 * quitação. O mapper media os juros como (total das parcelas − valorDivida),
 * então o erro crescia a cada parcela paga e, no fim, os juros engoliam a
 * dívida inteira.
 *
 * <p>A referência agora é {@code valorContratado}, que não muda nunca.
 */
class DividaMapperTest {

    @Test
    void dividaQuitadaNaoTransformaOTotalEmJuros() {
        // R$ 1.000 contratados, R$ 1.200 pagos em 2 parcelas: R$ 200 de juros.
        Divida divida = divida(BigDecimal.ZERO, new BigDecimal("1000"), StatusDivida.PAGA);
        divida.setParcelas(List.of(
                parcela(divida, 1, "600", "500", "100", StatusParcela.PAGA),
                parcela(divida, 2, "600", "500", "100", StatusParcela.PAGA)));

        var r = DividaMapper.toResponse(divida);

        assertThat(r.totalAPagar()).isEqualByComparingTo("1200");
        assertThat(r.totalJuros()).isEqualByComparingTo("200");
        assertThat(r.totalJuros()).isNotEqualByComparingTo(r.totalAPagar());
    }

    /** O erro antigo crescia junto com o pagamento, não só no fim. */
    @Test
    void jurosNaoCrescemConformeAsParcelasSaoPagas() {
        Divida naoPaga = divida(new BigDecimal("1000"), new BigDecimal("1000"), StatusDivida.ATIVA);
        naoPaga.setParcelas(List.of(
                parcela(naoPaga, 1, "600", "500", "100", StatusParcela.PENDENTE),
                parcela(naoPaga, 2, "600", "500", "100", StatusParcela.PENDENTE)));

        // Metade amortizada: saldo caiu para 500, o contratado continua 1000.
        Divida meioPaga = divida(new BigDecimal("500"), new BigDecimal("1000"), StatusDivida.ATIVA);
        meioPaga.setParcelas(List.of(
                parcela(meioPaga, 1, "600", "500", "100", StatusParcela.PAGA),
                parcela(meioPaga, 2, "600", "500", "100", StatusParcela.PENDENTE)));

        assertThat(DividaMapper.toResponse(meioPaga).totalJuros())
                .isEqualByComparingTo(DividaMapper.toResponse(naoPaga).totalJuros());
    }

    /**
     * Dívida gravada antes da coluna existir: o principal sai da soma das
     * parcelas, que também não muda com o pagamento.
     */
    @Test
    void semValorContratadoCaiNaSomaDoPrincipalDasParcelas() {
        Divida antiga = divida(BigDecimal.ZERO, null, StatusDivida.PAGA);
        antiga.setParcelas(List.of(
                parcela(antiga, 1, "600", "500", "100", StatusParcela.PAGA),
                parcela(antiga, 2, "600", "500", "100", StatusParcela.PAGA)));

        var r = DividaMapper.toResponse(antiga);

        assertThat(r.totalJuros()).isEqualByComparingTo("200");
    }

    @Test
    void statusChegaNaResposta() {
        Divida divida = divida(BigDecimal.ZERO, new BigDecimal("1000"), StatusDivida.PAGA);
        divida.setParcelas(List.of(parcela(divida, 1, "1200", "1000", "200", StatusParcela.PAGA)));

        assertThat(DividaMapper.toResponse(divida).status()).isEqualTo(StatusDivida.PAGA);
    }

    /** Dívida sem cronograma não estoura nem inventa juros. */
    @Test
    void semParcelasOsTotaisSaoZero() {
        Divida divida = divida(new BigDecimal("1000"), new BigDecimal("1000"), StatusDivida.ATIVA);
        divida.setParcelas(List.of());

        var r = DividaMapper.toResponse(divida);

        assertThat(r.totalAPagar()).isEqualByComparingTo("0");
        assertThat(r.totalJuros()).isEqualByComparingTo("0");
    }

    // --- montagem ---------------------------------------------------------

    private Divida divida(BigDecimal saldoAtual, BigDecimal contratado, StatusDivida status) {
        return Divida.builder()
                .id(1L)
                .idUsuario(7L)
                .nomeDivida("Cartão")
                .banco("Nubank")
                .valorDivida(saldoAtual)
                .valorContratado(contratado)
                .taxaJuros(new BigDecimal("2"))
                .parcelaMinima(new BigDecimal("600"))
                .quantidadeParcelas(2)
                .status(status)
                .build();
    }

    private ParcelaDivida parcela(
            Divida divida, int numero, String total, String principal, String juros, StatusParcela status) {
        return ParcelaDivida.builder()
                .id((long) numero)
                .divida(divida)
                .numeroParcela(numero)
                .valorTotal(new BigDecimal(total))
                .valorPrincipal(new BigDecimal(principal))
                .valorJuros(new BigDecimal(juros))
                .status(status)
                .dataVencimento(LocalDate.now().plusMonths(numero))
                .build();
    }
}
