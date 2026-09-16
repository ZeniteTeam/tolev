package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.finance.application.dto.response.BancoUsuarioResponse;
import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.ExtratoExtraido;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.Lancamento;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.TipoLancamento;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.TipoTransacaoMaisFrequente;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetExtratoImportsService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetUserBanksService;
import com.br.startup.tolevBack.finance.internal.entity.Banco;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.finance.internal.repository.IBankRepository;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.users.application.dto.request.RegisterRequest;
import com.br.startup.tolevBack.users.application.usecase.commands.RegisterUserService;
import com.br.startup.tolevBack.users.internal.enums.ObjetivoPrincipal;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import com.br.startup.tolevBack.users.internal.enums.TipoEmprego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * As guardas da importação de extrato, que existem porque não há tela de revisão
 * entre o que o Gemini leu e o que vai para o banco de dados.
 *
 * <p>O extrator é mockado de propósito: o que está sob teste é o que fazemos com
 * a leitura, não a leitura em si — essa já é coberta por
 * {@code ExtratoAnaliseServiceTest}.
 *
 * <p>Os testes passam pelo caminho assíncrono de verdade, e não chamam a
 * gravação direto: o objetivo é justamente provar que a importação chega ao fim
 * sozinha, depois de o POST já ter respondido. Como o Gemini está mockado, a
 * leitura termina em milissegundos e {@link #aguardarDesfecho} raramente espera.
 */
@SpringBootTest
@ActiveProfiles("test")
class ImportExtratoServiceTest {

    private static final AtomicLong PROXIMO_USUARIO = new AtomicLong(9000);
    private static final LocalDate HOJE = LocalDate.now();

    @MockitoBean ExtratoAnaliseService extratoAnalise;

    @Autowired ImportExtratoService importExtrato;
    @Autowired UndoExtratoImportService undoExtratoImport;
    @Autowired ConfirmExtratoImportService confirmExtratoImport;
    @Autowired GetExtratoImportsService getExtratoImports;
    @Autowired GetUserBanksService getUserBanks;
    @Autowired ITransactionRepository transactionRepository;
    @Autowired IImportacaoExtratoRepository importacaoRepository;
    @Autowired IBankRepository bankRepository;
    @Autowired ICategoriaGastoSistemaRepository categoriaRepository;
    @Autowired RegisterUserService registerUser;
    @Autowired IAnaliseRepository analiseRepository;

    private Long idUsuario;
    private Long idBanco;

    @BeforeEach
    void setUp() {
        // Usuário de verdade: o listener de análise roda depois de cada import e
        // um id inventado só produziria erro no log, escondendo falha real.
        long n = PROXIMO_USUARIO.incrementAndGet();
        idUsuario = registerUser.execute(new RegisterRequest(
                "Extrato " + n, "F", LocalDate.of(1996, 5, 20),
                ObjetivoPrincipal.QUITAR_DIVIDAS, SituacaoFinanceira.NO_LIMITE,
                TipoEmprego.CLT, new BigDecimal("5000.00"),
                "extrato" + n, "extrato" + n + "@tolev.test", "senha12345")).usuario().id();
        idBanco = bankRepository.save(Banco.builder().titulo("Banco Teste").build()).getId();
        if (categoriaRepository.findByAtivoTrueOrderByNomeAsc().isEmpty()) {
            // O perfil de teste sobe com ddl-auto, sem o seed do Flyway.
            categoriaRepository.save(categoria("Alimentação", TipoCategoriaGasto.DESPESA));
            categoriaRepository.save(categoria("Outros", TipoCategoriaGasto.DESPESA));
            categoriaRepository.save(categoria("Salário", TipoCategoriaGasto.RECEITA));
        }
    }

    @Test
    void uploadRespondeAntesDeLerOPdf() {
        extratoCom(saida(10, "mercado"));

        ExtratoImportacaoResponse aceito = importExtrato.execute(idUsuario, idBanco, "agosto.pdf", pdf());

        // Nada de resultado ainda: o POST só registrou o pedido.
        assertThat(aceito.status()).isEqualTo(StatusImportacaoExtrato.PROCESSANDO);
        assertThat(aceito.nomeArquivo()).isEqualTo("agosto.pdf");
        assertThat(aceito.confirmada()).isFalse();
        assertThat(aceito.lancamentosImportados()).isNull();

        assertThat(aguardarDesfecho(aceito.id()).status()).isEqualTo(StatusImportacaoExtrato.CONCLUIDA);
    }

    @Test
    void segundoExtratoImportaSoOQuePassaDoMarco() {
        // Extrato 1: cobre até 20 dias atrás.
        extratoCom(saida(30, "mercado"), saida(20, "farmacia"));
        ExtratoImportacaoResponse primeiro = importar();
        assertThat(primeiro.lancamentosImportados()).isEqualTo(2);
        assertThat(primeiro.marcoAnterior()).isNull();
        assertThat(primeiro.periodoFim()).isEqualTo(dia(20));

        // Extrato 2: o mensal, que repete o começo e traz o resto do mês.
        extratoCom(saida(30, "mercado"), saida(20, "farmacia"), saida(10, "posto"), saida(5, "padaria"));
        ExtratoImportacaoResponse segundo = importar();

        assertThat(segundo.lancamentosLidos()).isEqualTo(4);
        assertThat(segundo.lancamentosImportados()).as("só o que passa do marco").isEqualTo(2);
        assertThat(segundo.lancamentosPulados()).isEqualTo(2);
        assertThat(segundo.marcoAnterior()).isEqualTo(dia(20));
        assertThat(transacoesDoUsuario()).hasSize(4);
    }

    @Test
    void extratoInteiramenteCobertoFalhaComMotivoLegivel() {
        extratoCom(saida(30, "mercado"), saida(20, "farmacia"));
        importar();

        extratoCom(saida(30, "mercado"), saida(25, "outra"));
        ExtratoImportacaoResponse repetido = importar();

        // Não dá para "recusar" um upload que já foi aceito com 202: o jeito de
        // dizer não, agora, é terminar em FALHOU com o motivo escrito.
        assertThat(repetido.status()).isEqualTo(StatusImportacaoExtrato.FALHOU);
        assertThat(repetido.erro()).contains("já está importado até " + dia(20));
        assertThat(transacoesDoUsuario()).as("nada de novo entrou").hasSize(2);
    }

    @Test
    void marcoDeUmBancoNaoBloqueiaOutro() {
        extratoCom(saida(10, "mercado"));
        importar();

        Long outroBanco = bankRepository.save(Banco.builder().titulo("Outro Banco").build()).getId();
        extratoCom(saida(30, "aluguel"), saida(15, "luz"));

        ExtratoImportacaoResponse resposta = aguardarDesfecho(
                importExtrato.execute(idUsuario, outroBanco, "outro.pdf", pdf()).id());

        assertThat(resposta.lancamentosImportados())
                .as("o extrato do segundo banco entra inteiro, mesmo com datas anteriores")
                .isEqualTo(2);
        assertThat(resposta.marcoAnterior()).isNull();
    }

    @Test
    void lancamentoNoFuturoEDescartadoParaNaoEnvenenarOMarco() {
        // Ano errado ao completar "15/08" sem ano: se entrasse, o marco iria
        // junto para o futuro e travaria todo upload seguinte deste banco.
        extratoCom(saida(10, "mercado"),
                new Lancamento(TipoLancamento.SAIDA, new BigDecimal("50.00"),
                        "compra futura", HOJE.plusDays(30), "Outros"));

        ExtratoImportacaoResponse resposta = importar();

        assertThat(resposta.lancamentosImportados()).isEqualTo(1);
        assertThat(resposta.periodoFim()).isEqualTo(dia(10));
        assertThat(transacoesDoUsuario())
                .extracting(Transacao::getDataTransacao)
                .allSatisfy(data -> assertThat(data).isBeforeOrEqualTo(HOJE));
    }

    @Test
    void categoriaIncoerenteComOTipoViraSemCategoria() {
        // "Salário" é categoria de RECEITA; numa saída seria categoria trocada,
        // e categoria trocada envenena o donut e o ConsumoAnalyzer.
        extratoCom(
                new Lancamento(TipoLancamento.SAIDA, new BigDecimal("80.00"), "mercado", dia(9), "Salário"),
                new Lancamento(TipoLancamento.SAIDA, new BigDecimal("40.00"), "almoço", dia(8), "Alimentação"));

        ExtratoImportacaoResponse resposta = importar();

        assertThat(resposta.semCategoria()).isEqualTo(1);
        assertThat(transacoesDoUsuario())
                .filteredOn(t -> "mercado".equals(t.getDescricao()))
                .singleElement()
                .satisfies(t -> assertThat(t.getCategoriaGastoSistema()).isNull());
        assertThat(transacoesDoUsuario())
                .filteredOn(t -> "almoço".equals(t.getDescricao()))
                .singleElement()
                .satisfies(t -> assertThat(t.getCategoriaGastoSistema().getNome()).isEqualTo("Alimentação"));
    }

    @Test
    void gravaOBancoMasNaoVinculaContaNemInventaVendedor() {
        extratoCom(saida(10, "mercado"), entrada(9, "salario"));

        importar();

        assertThat(transacoesDoUsuario()).allSatisfy(t -> {
            assertThat(t.getBanco().getId())
                    .as("procedência do lançamento — é o que faz o filtro por banco funcionar")
                    .isEqualTo(idBanco);
            assertThat(t.getContaBancaria()).as("extrato não mexe em saldo de conta").isNull();
            assertThat(t.getVendedor()).as("descrição de extrato não é estabelecimento").isNull();
            assertThat(t.getMetodoPagamento()).isNull();
            assertThat(t.getIdImportacaoExtrato()).isNotNull();
        });
        assertThat(transacoesDoUsuario()).extracting(Transacao::getTipo)
                .containsExactlyInAnyOrder(TipoTransacao.DESPESA, TipoTransacao.RECEITA);
    }

    @Test
    void pdfDeMentiraERecusadoAntesDeChegarAoGemini() {
        // O content-type do multipart é escolhido pelo cliente; a assinatura do
        // arquivo não. Recusar aqui evita pagar uma chamada de API por lixo.
        assertThatThrownBy(() ->
                importExtrato.execute(idUsuario, idBanco, "extrato.pdf", "PK zip".getBytes()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("só aceitamos extrato em PDF");

        assertThat(importacaoRepository.findByIdUsuarioOrderByCriadoEmDesc(idUsuario))
                .as("nem chega a virar importação")
                .isEmpty();
    }

    @Test
    void doisExtratosAoMesmoTempoSaoRecusados() {
        // Duas importações simultâneas do mesmo banco leriam o mesmo marco e as
        // duas se achariam a primeira, duplicando o período em comum.
        importacaoRepository.save(com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato.builder()
                .idUsuario(idUsuario)
                .banco(bankRepository.findById(idBanco).orElseThrow())
                .status(StatusImportacaoExtrato.PROCESSANDO)
                .criadoEm(java.time.LocalDateTime.now())
                .build());

        assertThatThrownBy(() -> importExtrato.execute(idUsuario, idBanco, "outro.pdf", pdf()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já tem um extrato sendo lido");
    }

    @Test
    void confirmarMarcaQueOUsuarioViuOResultado() {
        extratoCom(saida(10, "mercado"));
        ExtratoImportacaoResponse pronta = importar();
        assertThat(pronta.confirmada()).as("recém-terminada, ainda não vista").isFalse();

        assertThat(confirmExtratoImport.execute(idUsuario, pronta.id()).confirmada()).isTrue();
        assertThat(getExtratoImports.byId(idUsuario, pronta.id()).confirmada())
                .as("sobrevive ao app ser fechado")
                .isTrue();
    }

    @Test
    void extratoImportadoChegaAoMotorDeAnalise() {
        // O ponto do recurso inteiro: subiu o extrato, a análise do gasto recente
        // sai sem esperar o debounce de 30 minutos. Só acontece porque a
        // importação publica EXTRATO_IMPORTADO, que é de alto impacto.
        assertThat(OrigemAlteracao.EXTRATO_IMPORTADO.isAltoImpacto()).isTrue();

        extratoCom(saida(10, "mercado"), saida(8, "posto"), entrada(9, "salario"));

        importar();

        assertThat(aguardar(() -> analiseRepository.findAll().stream()
                .filter(a -> idUsuario.equals(a.getIdUsuario()))
                .toList()))
                .as("importar extrato deve gerar análise na hora")
                .isNotEmpty();
    }

    @Test
    void bancosDoUsuarioResumemOQueJaEntrou() {
        extratoCom(saida(20, "mercado"), entrada(10, "salario"));
        importar();

        List<BancoUsuarioResponse> bancos = getUserBanks.execute(idUsuario);

        assertThat(bancos).singleElement().satisfies(b -> {
            assertThat(b.idBanco()).isEqualTo(idBanco);
            assertThat(b.nome()).isEqualTo("Banco Teste");
            assertThat(b.quantidadeTransacoes()).isEqualTo(2);
            assertThat(b.totalSaidas()).isEqualByComparingTo("100.00");
            assertThat(b.totalEntradas()).isEqualByComparingTo("4000.00");
            assertThat(b.importadoAte()).as("o marco, que explica o próximo upload").isEqualTo(dia(10));
        });
    }

    @Test
    void desfazerApagaAsTransacoesELiberaOPeriodo() {
        extratoCom(saida(20, "mercado"), saida(10, "posto"));
        ExtratoImportacaoResponse resposta = importar();
        assertThat(transacoesDoUsuario()).hasSize(2);

        undoExtratoImport.execute(idUsuario, resposta.id());

        assertThat(transacoesDoUsuario()).isEmpty();
        assertThat(importacaoRepository.findById(resposta.id())).isEmpty();
        assertThat(getUserBanks.execute(idUsuario)).as("o banco some junto com as transações").isEmpty();

        // Marco liberado: o mesmo período pode ser subido de novo.
        extratoCom(saida(20, "mercado"), saida(10, "posto"));
        assertThat(importar().lancamentosImportados()).isEqualTo(2);
    }

    @Test
    void desfazerImportacaoDeOutroUsuarioERecusado() {
        extratoCom(saida(10, "mercado"));
        Long idImportacao = importar().id();

        assertThatThrownBy(() -> undoExtratoImport.execute(idUsuario + 1, idImportacao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao usuário");
    }

    // ---------- fixtures ----------

    /** Sobe o extrato e espera a leitura terminar, como a tela faz. */
    private ExtratoImportacaoResponse importar() {
        return aguardarDesfecho(importExtrato.execute(idUsuario, idBanco, "extrato.pdf", pdf()).id());
    }

    /** Relê a importação até ela sair de PROCESSANDO — o mesmo que o app faz. */
    private ExtratoImportacaoResponse aguardarDesfecho(Long idImportacao) {
        for (int i = 0; i < 100; i++) {
            ExtratoImportacaoResponse atual = getExtratoImports.byId(idUsuario, idImportacao);
            if (atual.status() != StatusImportacaoExtrato.PROCESSANDO) {
                return atual;
            }
            dormir();
        }
        throw new AssertionError("A importação " + idImportacao + " não terminou a tempo.");
    }

    private void extratoCom(Lancamento... lancamentos) {
        List<Lancamento> lista = List.of(lancamentos);
        when(extratoAnalise.analisar(any(), anyList())).thenReturn(new ExtratoExtraido(
                lista, BigDecimal.ZERO, BigDecimal.ZERO,
                TipoLancamento.SAIDA, TipoTransacaoMaisFrequente.PIX, lista.get(0)));
    }

    private Lancamento saida(int diasAtras, String descricao) {
        return new Lancamento(TipoLancamento.SAIDA, new BigDecimal("100.00"),
                descricao, dia(diasAtras), "Outros");
    }

    private Lancamento entrada(int diasAtras, String descricao) {
        return new Lancamento(TipoLancamento.ENTRADA, new BigDecimal("4000.00"),
                descricao, dia(diasAtras), "Salário");
    }

    private LocalDate dia(int diasAtras) {
        return HOJE.minusDays(diasAtras);
    }

    private byte[] pdf() {
        return "%PDF-1.4 extrato".getBytes();
    }

    private List<Transacao> transacoesDoUsuario() {
        return transactionRepository.findByIdUsuarioOrderByDataTransacaoDescIdDesc(idUsuario);
    }

    /** O listener é @Async: espera o pool terminar antes de conferir o banco. */
    private <T> List<T> aguardar(Supplier<List<T>> consulta) {
        List<T> resultado = List.of();
        for (int i = 0; i < 60 && resultado.isEmpty(); i++) {
            resultado = consulta.get();
            if (!resultado.isEmpty()) {
                return resultado;
            }
            dormir();
        }
        return resultado;
    }

    private void dormir() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private CategoriaGastoSistema categoria(String nome, TipoCategoriaGasto tipo) {
        return CategoriaGastoSistema.builder()
                .nome(nome).cor("#000000").tipo(tipo).ativo(true).build();
    }
}
