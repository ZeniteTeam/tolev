package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest.ParcelaPaga;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IPagamentoParcelaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IParcelaDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterDividaPaymentServiceTest {

    @Mock private IDividaRepository dividaRepository;
    @Mock private IParcelaDividaRepository parcelaDividaRepository;
    @Mock private IPagamentoParcelaRepository pagamentoParcelaRepository;
    @Mock private IProgressoDividaRepository progressoDividaRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private RegisterDividaPaymentService service;

    private Divida divida;
    private List<ParcelaDivida> parcelas;

    /**
     * Dívida de R$ 2.000 em 3x com juros: as parcelas somam R$ 2.058, sendo
     * R$ 2.000 de principal e R$ 58 de juros.
     */
    @BeforeEach
    void setUp() {
        divida = Divida.builder()
                .id(1L)
                .valorDivida(new BigDecimal("2000.00"))
                .quantidadeParcelas(3)
                .status(StatusDivida.ATIVA)
                .build();

        parcelas = new ArrayList<>(List.of(
                parcela(1, "686.00", "656.00", "30.00"),
                parcela(2, "686.00", "666.00", "20.00"),
                parcela(3, "686.00", "678.00", "8.00")));
        divida.setParcelas(parcelas);

        when(dividaRepository.findById(1L)).thenReturn(Optional.of(divida));
        when(dividaRepository.save(any(Divida.class))).thenAnswer(i -> i.getArgument(0));
        when(progressoDividaRepository.findByDivida(divida)).thenReturn(Optional.of(progresso()));
        when(parcelaDividaRepository.findByDividaOrderByNumeroParcela(divida)).thenReturn(parcelas);
        when(parcelaDividaRepository.findByDividaAndNumeroParcelaIn(any(), any()))
                .thenAnswer(i -> {
                    List<Integer> numeros = i.getArgument(1);
                    return parcelas.stream().filter(p -> numeros.contains(p.getNumeroParcela())).toList();
                });
    }

    private ParcelaDivida parcela(int numero, String total, String principal, String juros) {
        return ParcelaDivida.builder()
                .id((long) numero)
                .numeroParcela(numero)
                .valorTotal(new BigDecimal(total))
                .valorPrincipal(new BigDecimal(principal))
                .valorJuros(new BigDecimal(juros))
                .status(StatusParcela.PENDENTE)
                .build();
    }

    private ProgressoDivida progresso() {
        ProgressoDivida p = new ProgressoDivida();
        p.setDivida(divida);
        p.setProgresso(BigDecimal.ZERO);
        return p;
    }

    private RegisterPaymentRequest pagar(int... numeros) {
        List<ParcelaPaga> pagas = new ArrayList<>();
        for (int n : numeros) {
            pagas.add(new ParcelaPaga(n, parcelas.get(n - 1).getValorTotal()));
        }
        return new RegisterPaymentRequest(1L, pagas);
    }

    @Test
    void saldoCaiPelaAmortizacaoENaoPeloValorCheioDaParcela() {
        service.execute(pagar(1));

        // Pagou R$ 686, mas só R$ 656 abatem o principal — R$ 30 eram juros.
        assertThat(divida.getValorDivida()).isEqualByComparingTo("1344.00");
    }

    @Test
    void pagarTodasAsParcelasZeraOSaldoEQuitaADivida() {
        service.execute(pagar(1, 2, 3));

        assertThat(divida.getValorDivida()).isEqualByComparingTo("0.00");
        assertThat(divida.getStatus()).isEqualTo(StatusDivida.PAGA);
        assertThat(parcelas).allSatisfy(p -> assertThat(p.getStatus()).isEqualTo(StatusParcela.PAGA));
    }

    @Test
    void cadaParcelaUsaOProprioValorInformado() {
        // Num SAC as parcelas diferem entre si: os valores não podem ser rateados
        // a partir de um único "valor por parcela".
        service.execute(new RegisterPaymentRequest(1L, List.of(
                new ParcelaPaga(1, new BigDecimal("686.00")),
                new ParcelaPaga(2, new BigDecimal("500.00")))));

        // Amortização prevista das duas: 656 + 666 = 1.322.
        assertThat(divida.getValorDivida()).isEqualByComparingTo("678.00");
    }

    @Test
    void pagamentoAcimaDoPrevistoAbateOExcedenteNoPrincipal() {
        service.execute(new RegisterPaymentRequest(1L, List.of(
                new ParcelaPaga(1, new BigDecimal("886.00")))));

        // 656 de amortização + 200 pagos a mais = 856 abatidos.
        assertThat(divida.getValorDivida()).isEqualByComparingTo("1144.00");
    }

    @Test
    void parcelaJaPagaNaoAbateOSaldoDeNovo() {
        parcelas.get(0).setStatus(StatusParcela.PAGA);

        service.execute(pagar(1));

        assertThat(divida.getValorDivida()).isEqualByComparingTo("2000.00");
    }

    @Test
    void semValorInformadoAssumeOValorDaPropriaParcela() {
        service.execute(new RegisterPaymentRequest(1L, List.of(new ParcelaPaga(2, null))));

        assertThat(divida.getValorDivida()).isEqualByComparingTo("1334.00");
    }

    @Test
    void listaDeParcelasVaziaERejeitada() {
        assertThatThrownBy(() -> service.execute(new RegisterPaymentRequest(1L, List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nenhuma parcela");
    }
}
