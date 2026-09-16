package com.br.startup.tolevBack.finance.application.service;

import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.ExtratoExtraido;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.Lancamento;
import com.br.startup.tolevBack.finance.application.service.ExtratoAnaliseService.TipoLancamento;
import com.br.startup.tolevBack.finance.exception.ExtratoSemNovidadeException;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.enums.StatusImportacaoExtrato;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.finance.internal.repository.IImportacaoExtratoRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.finance.internal.util.TextNormalizer;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * O passo que grava: transforma os lançamentos lidos em transações e fecha a
 * importação.
 *
 * <p>Separado do {@code ProcessExtratoService} porque só ele precisa de
 * transação. A chamada ao Gemini leva dezenas de segundos e fica de fora de
 * propósito: manter uma conexão do pool aberta esperando resposta HTTP externa
 * esgotaria o pool com poucos uploads simultâneos.
 *
 * <p>As três guardas da importação moram aqui, porque não existe tela de revisão
 * entre o que o modelo leu e o que vai para o banco de dados:
 *
 * <p><strong>Marco por banco.</strong> Cada importação registra até que data
 * aquele banco foi coberto. A próxima só grava lançamento posterior a essa data,
 * então subir o extrato de setembro depois do de agosto não reimporta agosto — e
 * o extrato mensal que sempre pega o mês inteiro entra sem ser recusado, perdendo
 * só a parte repetida.
 *
 * <p><strong>Nada de saldo.</strong> O extrato descreve o que já aconteceu na
 * conta do banco; reaplicar cada linha no saldo de {@code ContaBancaria} contaria
 * o mesmo dinheiro duas vezes. É a diferença central para
 * {@code CreateTransactionService}, que move o saldo de propósito.
 *
 * <p><strong>Data no futuro é lixo, não dado.</strong> Um extrato não fala do
 * futuro; se veio data adiante, o modelo errou o ano ao completar um "15/08" sem
 * ano. Deixar passar seria pior do que perder a linha: o marco iria junto para o
 * futuro e travaria todas as importações seguintes daquele banco.
 */
@Service
@RequiredArgsConstructor
public class ExtratoGravacaoService {

    private final ITransactionRepository transactionRepository;
    private final IImportacaoExtratoRepository importacaoRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * @return quantas transações entraram; nunca zero, porque o caso "não sobrou
     *         nada" vira exceção antes
     * @throws ExtratoSemNovidadeException quando o PDF foi lido mas não há o que
     *         gravar. Não é defeito técnico, e a mensagem vai direto para a tela
     */
    @Transactional
    public int gravar(Long idImportacao, ExtratoExtraido extrato, List<CategoriaGastoSistema> catalogo) {
        ImportacaoExtrato importacao = importacaoRepository.getReferenceById(idImportacao);

        List<Lancamento> lidos = extrato.transacoes() != null ? extrato.transacoes() : List.of();
        if (lidos.isEmpty()) {
            throw new ExtratoSemNovidadeException(
                    "Não encontramos nenhum lançamento nesse extrato. Confira se o PDF é o arquivo certo.");
        }

        LocalDate marco = marcoAnterior(importacao);
        List<Lancamento> novos = lidos.stream().filter(l -> aproveitavel(l, marco)).toList();

        if (novos.isEmpty()) {
            throw new ExtratoSemNovidadeException(marco == null
                    ? "Nenhum lançamento desse extrato pôde ser aproveitado."
                    : "Esse extrato não traz nada novo — esse banco já está importado até "
                            + marco + ".");
        }

        Map<String, CategoriaGastoSistema> porNome = catalogo.stream()
                .collect(Collectors.toMap(
                        CategoriaGastoSistema::getNome, Function.identity(), (a, b) -> a));

        List<Transacao> transacoes = new ArrayList<>(novos.size());
        int semCategoria = 0;
        for (Lancamento lancamento : novos) {
            CategoriaGastoSistema categoria = categoriaDe(lancamento, porNome);
            if (categoria == null) {
                semCategoria++;
            }
            transacoes.add(paraTransacao(lancamento, importacao, categoria));
        }

        List<Transacao> salvas = transactionRepository.saveAll(transacoes);

        importacao.setStatus(StatusImportacaoExtrato.CONCLUIDA);
        importacao.setMarcoAnterior(marco);
        importacao.setDataInicio(extremo(novos, true));
        importacao.setDataFim(extremo(novos, false));
        importacao.setQuantidadeLida(lidos.size());
        importacao.setQuantidadeImportada(salvas.size());
        importacao.setQuantidadePulada(lidos.size() - novos.size());
        importacao.setQuantidadeSemCategoria(semCategoria);
        importacao.setTotalEntradas(extrato.totalEntradas());
        importacao.setTotalSaidas(extrato.totalSaidas());
        importacao.setConcluidoEm(LocalDateTime.now());

        // Publicado daqui de dentro, e não do orquestrador assíncrono, porque a
        // análise escuta com AFTER_COMMIT: fora de uma transação o evento seria
        // descartado em silêncio e a nota do usuário nunca recalcularia.
        //
        // Um evento por importação, não um por lançamento: o que mudou é "o
        // extrato entrou", e recalcular a análise trinta vezes seguidas daria o
        // mesmo resultado da última.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                importacao.getIdUsuario(), OrigemAlteracao.EXTRATO_IMPORTADO,
                "IMPORTACAO_EXTRATO", importacao.getId()));

        return salvas.size();
    }

    /**
     * Registra o fracasso na própria importação, em transação separada — é o que
     * tira o upload de {@code PROCESSANDO} e faz a tela sair da espera.
     */
    @Transactional
    public void falhar(Long idImportacao, String motivo) {
        ImportacaoExtrato importacao = importacaoRepository.getReferenceById(idImportacao);
        importacao.setStatus(StatusImportacaoExtrato.FALHOU);
        importacao.setErro(motivo);
        importacao.setConcluidoEm(LocalDateTime.now());
    }

    /** Data final da última importação concluída deste banco; {@code null} na primeira. */
    private LocalDate marcoAnterior(ImportacaoExtrato importacao) {
        return importacaoRepository
                .findTopByIdUsuarioAndBancoIdAndStatusOrderByDataFimDesc(
                        importacao.getIdUsuario(),
                        importacao.getBanco().getId(),
                        StatusImportacaoExtrato.CONCLUIDA)
                .map(ImportacaoExtrato::getDataFim)
                .orElse(null);
    }

    /**
     * Só entra o que é novo e utilizável. Valor e data faltando ou absurdos são
     * falha de leitura do PDF — a linha é perdida de propósito, em vez de virar
     * uma transação de R$ 0,00 que ninguém consegue explicar depois.
     */
    private boolean aproveitavel(Lancamento l, LocalDate marco) {
        if (l == null || l.dataTransacao() == null || l.tipo() == null) {
            return false;
        }
        if (l.valor() == null || l.valor().signum() <= 0) {
            return false;
        }
        if (l.dataTransacao().isAfter(LocalDate.now())) {
            return false;
        }
        return marco == null || l.dataTransacao().isAfter(marco);
    }

    private LocalDate extremo(List<Lancamento> lancamentos, boolean menor) {
        Comparator<Lancamento> porData = Comparator.comparing(Lancamento::dataTransacao);
        return (menor ? lancamentos.stream().min(porData) : lancamentos.stream().max(porData))
                .map(Lancamento::dataTransacao)
                .orElseThrow();
    }

    /**
     * O {@code responseSchema} do Gemini já garante que o nome existe no catálogo
     * — o que ele não garante é coerência: nada impede o modelo de carimbar
     * "Salário" numa saída. Categoria trocada envenenaria o donut de gastos e o
     * ConsumoAnalyzer, então incoerência vira sem categoria, não um palpite.
     */
    private CategoriaGastoSistema categoriaDe(Lancamento l, Map<String, CategoriaGastoSistema> porNome) {
        if (l.categoria() == null) {
            return null;
        }
        CategoriaGastoSistema categoria = porNome.get(l.categoria());
        if (categoria == null) {
            return null;
        }
        TipoCategoriaGasto esperado = l.tipo() == TipoLancamento.ENTRADA
                ? TipoCategoriaGasto.RECEITA
                : TipoCategoriaGasto.DESPESA;
        return categoria.getTipo() == esperado ? categoria : null;
    }

    /**
     * O banco vai gravado porque é a única procedência que sabemos com certeza —
     * o usuário escolheu antes de subir o PDF — e é o que faz o filtro por banco
     * da tela de Finanças deixar de ser enfeite.
     *
     * <p>Vendedor fica de fora: a descrição de extrato ("PIX ENVIADO 12/08",
     * "COMPRA CARTAO 4471") não é nome de estabelecimento, e
     * {@code tb_vendedores} é catálogo global — uma linha por texto desses
     * poluiria o catálogo de todos os usuários. Método de pagamento também: o
     * extrato só diz qual foi o mais frequente do documento, e aplicar isso a
     * cada lançamento seria inventar.
     */
    private Transacao paraTransacao(
            Lancamento l, ImportacaoExtrato importacao, CategoriaGastoSistema categoria) {
        return Transacao.builder()
                .idUsuario(importacao.getIdUsuario())
                .banco(importacao.getBanco())
                .valor(l.valor())
                .dataTransacao(l.dataTransacao())
                .tipo(l.tipo() == TipoLancamento.ENTRADA ? TipoTransacao.RECEITA : TipoTransacao.DESPESA)
                .descricao(l.descricao())
                .descricaoNormalizada(TextNormalizer.normalize(l.descricao()))
                .parcelado(false)
                .categoriaGastoSistema(categoria)
                .idImportacaoExtrato(importacao.getId())
                .build();
    }
}
