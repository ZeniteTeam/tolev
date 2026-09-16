package com.br.startup.tolevBack.analysis;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoVariavelRepository;
import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.CreateDividaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.RegisterDividaPaymentService;
import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;
import com.br.startup.tolevBack.users.application.dto.request.RegisterRequest;
import com.br.startup.tolevBack.users.application.dto.response.AuthResponse;
import com.br.startup.tolevBack.users.application.usecase.commands.RegisterUserService;
import com.br.startup.tolevBack.users.internal.enums.ObjetivoPrincipal;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import com.br.startup.tolevBack.users.internal.enums.TipoEmprego;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prova ponta a ponta de que uma operação do usuário chega ao motor de análise:
 * cadastro → dívida → pagamento de parcela, cada passo comitado de verdade,
 * conferindo o que foi gravado em tb_analises depois de cada um.
 */
@SpringBootTest
@ActiveProfiles("test")
class AnalysisPipelineTest {

    @Autowired RegisterUserService registerUser;
    @Autowired CreateDividaService createDivida;
    @Autowired RegisterDividaPaymentService registerPayment;
    @Autowired IAnaliseRepository analiseRepository;
    @Autowired IAnaliseResultadoRepository resultadoRepository;
    @Autowired IAnaliseResultadoVariavelRepository variavelRepository;

    @Test
    void operacaoDoUsuarioGeraAnalise() {
        // 1. Cadastro: renda obrigatória entra em PreferenciaFinanceira.
        AuthResponse auth = registerUser.execute(new RegisterRequest(
                "Marina", "F", LocalDate.of(1995, 3, 10),
                ObjetivoPrincipal.QUITAR_DIVIDAS, SituacaoFinanceira.NO_LIMITE,
                TipoEmprego.CLT, new BigDecimal("4000.00"),
                "marina", "marina@tolev.test", "senha12345"));
        Long idUsuario = auth.usuario().id();

        // 2. Criar dívida — evento DIVIDA_CRIADA (alto impacto, fura o debounce).
        DividaResponse divida = createDivida.execute(new DividaRequest(
                idUsuario, "Cartão", "Nubank", TipoDivida.CARTAO,
                new BigDecimal("3000.00"), new BigDecimal("12.00"),
                new BigDecimal("2.00"), new BigDecimal("1.00"),
                3, 10, LocalDate.now().minusMonths(1), LocalDate.now().plusDays(5),
                SistemaAmortizacao.PRICE, RegimeJuros.COMPOSTO));

        List<Analise> aposDivida = aguardar(
                () -> analiseRepository.findAll().stream()
                        .filter(a -> idUsuario.equals(a.getIdUsuario()))
                        .toList());

        assertThat(aposDivida)
                .as("criar dívida deve gerar análises")
                .isNotEmpty();
        System.out.println(">>> Após criar dívida: " + aposDivida.size() + " análises "
                + aposDivida.stream().map(a -> String.valueOf(a.getTipo())).toList());

        long variaveisAposDivida = variavelRepository.count();
        assertThat(resultadoRepository.count()).as("cada análise grava um resultado").isPositive();
        assertThat(variaveisAposDivida).as("cada resultado grava suas variáveis").isPositive();

        // 3. Pagar a primeira parcela — evento PAGAMENTO_DIVIDA (alto impacto).
        // A análise do dia é reaproveitada, mas dataCriacao é recarimbada: é isso
        // que prova que o motor rodou de novo por causa do pagamento.
        LocalDateTime marco = aposDivida.stream()
                .map(Analise::getDataCriacao)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        registerPayment.execute(new RegisterPaymentRequest(
                divida.id(),
                List.of(new RegisterPaymentRequest.ParcelaPaga(1, null))));

        List<Analise> aposPagamento = aguardar(
                () -> analiseRepository.findAll().stream()
                        .filter(a -> idUsuario.equals(a.getIdUsuario()))
                        .filter(a -> a.getDataCriacao().isAfter(marco))
                        .toList());

        System.out.println(">>> Após pagar parcela: " + aposPagamento.size()
                + " análises recalculadas; total de análises do usuário "
                + analiseRepository.findAll().stream()
                        .filter(a -> idUsuario.equals(a.getIdUsuario())).count()
                + "; variáveis " + variavelRepository.count());

        assertThat(aposPagamento)
                .as("pagar parcela deve recalcular as análises")
                .isNotEmpty();
    }

    /** O listener é @Async: espera o pool terminar antes de conferir o banco. */
    private <T> List<T> aguardar(Supplier<List<T>> consulta) {
        List<T> resultado = List.of();
        for (int i = 0; i < 60 && resultado.isEmpty(); i++) {
            resultado = consulta.get();
            if (!resultado.isEmpty()) {
                return resultado;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return resultado;
    }
}
