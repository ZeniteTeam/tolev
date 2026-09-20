package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.graphs.application.dto.response.DebtProjectionGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.DebtProjectionGraphResponse.MesProjetado;
import com.br.startup.tolevBack.graphs.application.dto.response.DebtProjectionGraphResponse.PontoProgresso;
import com.br.startup.tolevBack.graphs.internal.enums.NivelComprometimento;
import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import com.br.startup.tolevBack.users.internal.enums.MetodoOrcamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A projeção de quitação: quando a última parcela cai, quanto ainda falta e
 * como o saldo desce mês a mês até lá.
 *
 * <p>Tudo sai do <strong>cronograma de parcelas</strong>, não do progresso
 * percentual da dívida. São duas verdades diferentes: {@code ProgressoDivida}
 * guarda principal amortizado, enquanto a parcela carrega principal + juros e,
 * principalmente, carrega <em>data</em>. Sem data não existe projeção, e somar
 * as duas fontes faria o termômetro discordar do card de cima na mesma tela.
 *
 * <p>Parcela atrasada não fica no passado: ela é dinheiro devido agora, então
 * entra no balde do mês corrente. Isso também garante que a série sempre começa
 * hoje, mesmo quando todas as parcelas já venceram.
 */
@Service
@RequiredArgsConstructor
public class GetDebtProjectionGraphService {

    private static final BigDecimal CEM = new BigDecimal("100");

    /**
     * Metade da renda em dívida é crítico, venha o plano que vier. É a única
     * régua aqui que não é do usuário: um plano que reserva 60% para dívida
     * não torna 55% saudável.
     */
    private static final BigDecimal COMPROMETIMENTO_CRITICO = new BigDecimal("50");

    /** Usados quando a preferência veio sem percentuais — a 50/30/20 clássica. */
    private static final int PERC_DIVIDAS_PADRAO = 30;
    private static final int PERC_LAZER_PADRAO = 20;

    /** Até onde o gráfico de barras olha. A tela mostra 6; 12 deixa margem. */
    private static final int MESES_PROJETADOS = 12;

    /**
     * Teto de pontos da curva de progresso. Um financiamento de 60 meses vira
     * 60 pontos em 64px de altura — ruído, não informação.
     */
    private static final int MAX_PONTOS_CURVA = 24;

    private final ProgressionIntegrationApi progressionIntegrationApi;
    private final UserIntegrationApi userIntegrationApi;
    private final FinanceIntegrationApi financeIntegrationApi;

    public DebtProjectionGraphResponse execute(Long idUsuario) {
        List<ParcelaResponse> parcelas = progressionIntegrationApi.getDividasByUser(idUsuario).stream()
                .map(DividaResponse::parcelas)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();

        BigDecimal totalPago = soma(parcelas.stream()
                .filter(p -> p.status() == StatusParcela.PAGA)
                .map(ParcelaResponse::valorTotal));

        // Sem vencimento a parcela não projeta nada — não há mês onde colocá-la.
        List<ParcelaResponse> abertas = parcelas.stream()
                .filter(GetDebtProjectionGraphService::emAberto)
                .filter(p -> p.dataVencimento() != null)
                .sorted(Comparator.comparing(ParcelaResponse::dataVencimento))
                .toList();

        PreferenciaFinanceiraResponse prefs = userIntegrationApi.getPreferencias(idUsuario);
        BigDecimal rendaMensal = valor(prefs.rendaMensal());
        BigDecimal gastosFixos = gastosFixosMensais(idUsuario);

        if (abertas.isEmpty()) {
            return semDividaEmAberto(idUsuario, totalPago, prefs, rendaMensal, gastosFixos);
        }

        BigDecimal totalRestante = soma(abertas.stream().map(ParcelaResponse::valorTotal));
        BigDecimal totalJurosRestante = soma(abertas.stream().map(ParcelaResponse::valorJuros));
        BigDecimal totalContratado = totalPago.add(totalRestante);

        YearMonth hoje = YearMonth.now();
        LocalDate ultimoVencimento = abertas.get(abertas.size() - 1).dataVencimento();
        LocalDate dataPrevistaQuitacao = ultimoVencimento.isBefore(hoje.atEndOfMonth())
                ? hoje.atEndOfMonth()
                : ultimoVencimento;
        YearMonth mesQuitacao = YearMonth.from(dataPrevistaQuitacao);
        int mesesRestantes = (int) ChronoUnit.MONTHS.between(hoje, mesQuitacao) + 1;

        TreeMap<YearMonth, BigDecimal> porMes = new TreeMap<>();
        for (ParcelaResponse p : abertas) {
            YearMonth vencimento = YearMonth.from(p.dataVencimento());
            YearMonth balde = vencimento.isBefore(hoje) ? hoje : vencimento;
            porMes.merge(balde, valor(p.valorTotal()), BigDecimal::add);
        }

        // Mês sem parcela não some da série: ele existe no tempo, e pulá-lo
        // faria o eixo X mentir sobre a distância até a quitação.
        List<MesProjetado> meses = new ArrayList<>();
        List<PontoProgresso> curva = new ArrayList<>();
        BigDecimal saldo = totalRestante;
        BigDecimal acumuladoPago = totalPago;

        for (YearMonth m = hoje; !m.isAfter(mesQuitacao); m = m.plusMonths(1)) {
            BigDecimal pagamento = porMes.getOrDefault(m, BigDecimal.ZERO);
            saldo = saldo.subtract(pagamento);
            acumuladoPago = acumuladoPago.add(pagamento);

            if (meses.size() < MESES_PROJETADOS) {
                meses.add(new MesProjetado(m.toString(), escala(saldo), escala(pagamento)));
            }
            curva.add(new PontoProgresso(m.toString(), percentual(acumuladoPago, totalContratado)));
        }

        // O mês corrente já pode ter sido pago; o seguinte é o primeiro
        // compromisso inteiro, e é ele que mede o quanto a renda aguenta.
        BigDecimal pagamentoMensalPrevisto = porMes.getOrDefault(hoje.plusMonths(1), BigDecimal.ZERO);
        if (pagamentoMensalPrevisto.signum() == 0) {
            pagamentoMensalPrevisto = porMes.getOrDefault(hoje, BigDecimal.ZERO);
        }

        BigDecimal sobra = sobraMensal(rendaMensal, gastosFixos, pagamentoMensalPrevisto);
        BigDecimal limiteLazer = limiteLazerMensal(prefs, rendaMensal);
        BigDecimal comprometimento = percentual(pagamentoMensalPrevisto, rendaMensal);

        return new DebtProjectionGraphResponse(
                idUsuario,
                dataPrevistaQuitacao,
                mesesRestantes,
                escala(totalRestante),
                escala(totalPago),
                percentual(totalPago, totalContratado),
                escala(pagamentoMensalPrevisto),
                escala(totalJurosRestante),
                comprometimento,
                nivelComprometimento(comprometimento, rendaMensal, prefs).name(),
                new BigDecimal(percDividas(prefs)),
                escala(rendaMensal),
                metodo(prefs).name(),
                limiteLazer,
                escala(gastosFixos),
                sobra,
                porDia(sobra, limiteLazer),
                amostrar(curva),
                meses);
    }

    /**
     * Quitado, ou nunca endividado. Sem parcela em aberto não há data para
     * prever, e {@code dataPrevistaQuitacao} nulo é o sinal que a tela lê para
     * mostrar o estado vazio em vez de um gráfico de zeros.
     */
    private DebtProjectionGraphResponse semDividaEmAberto(
            Long idUsuario,
            BigDecimal totalPago,
            PreferenciaFinanceiraResponse prefs,
            BigDecimal rendaMensal,
            BigDecimal gastosFixos) {
        // Sem parcela, a sobra é renda menos os fixos — a conta continua valendo,
        // e é justamente aqui que ela fica mais alta.
        BigDecimal sobra = sobraMensal(rendaMensal, gastosFixos, BigDecimal.ZERO);
        BigDecimal limiteLazer = limiteLazerMensal(prefs, rendaMensal);
        // Sem dívida o comprometimento é zero, e zero nunca é preocupante.
        NivelComprometimento nivel = rendaMensal.signum() <= 0
                ? NivelComprometimento.SEM_RENDA
                : NivelComprometimento.SAUDAVEL;

        return new DebtProjectionGraphResponse(
                idUsuario,
                null,
                0,
                BigDecimal.ZERO,
                escala(totalPago),
                totalPago.signum() > 0 ? escala(CEM) : BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                nivel.name(),
                new BigDecimal(percDividas(prefs)),
                escala(rendaMensal),
                metodo(prefs).name(),
                limiteLazer,
                escala(gastosFixos),
                sobra,
                porDia(sobra, limiteLazer),
                List.of(),
                List.of());
    }

    private MetodoOrcamento metodo(PreferenciaFinanceiraResponse prefs) {
        return prefs.metodoOrcamento() != null
                ? prefs.metodoOrcamento()
                : MetodoOrcamento.REGRA_50_30_20;
    }

    private int percDividas(PreferenciaFinanceiraResponse prefs) {
        return prefs.percDividas() != null ? prefs.percDividas() : PERC_DIVIDAS_PADRAO;
    }

    /**
     * O teto mensal de gasto livre que o plano impõe.
     *
     * <p>Só a 50/30/20 impõe um: ela separa uma fatia da renda para lazer, e
     * gastar acima dela é furar o plano mesmo sobrando dinheiro em conta.
     *
     * <p>No orçamento base zero não há teto a devolver — a premissa é que cada
     * real ganha uma função, então o que sobra depois de fixos e parcelas é
     * justamente o que está para ser distribuído. Envelopes ainda não tem
     * modelo de alocação e segue a mesma regra por ora.
     */
    private BigDecimal limiteLazerMensal(PreferenciaFinanceiraResponse prefs, BigDecimal renda) {
        if (metodo(prefs) != MetodoOrcamento.REGRA_50_30_20 || renda.signum() <= 0) {
            return null;
        }
        int percLazer = prefs.percLazer() != null ? prefs.percLazer() : PERC_LAZER_PADRAO;
        return renda.multiply(new BigDecimal(percLazer)).divide(CEM, 2, RoundingMode.HALF_UP);
    }

    /**
     * Bom ou ruim, com a régua do próprio usuário.
     *
     * <p>Acima de metade da renda é crítico sempre. Abaixo disso, a pergunta é
     * se passou do que ele mesmo planejou reservar para dívida — dizer que 35%
     * é "atenção" para quem planejou 30% e "tudo bem" para quem planejou 40% é
     * o que torna o aviso dele, e não de uma cartilha.
     */
    private NivelComprometimento nivelComprometimento(
            BigDecimal comprometimento, BigDecimal renda, PreferenciaFinanceiraResponse prefs) {
        if (renda.signum() <= 0) return NivelComprometimento.SEM_RENDA;
        if (comprometimento.compareTo(COMPROMETIMENTO_CRITICO) > 0) {
            return NivelComprometimento.CRITICO;
        }
        if (comprometimento.compareTo(new BigDecimal(percDividas(prefs))) > 0) {
            return NivelComprometimento.ATENCAO;
        }
        return NivelComprometimento.SAUDAVEL;
    }

    /**
     * Renda − gastos fixos − parcelas do mês.
     *
     * <p>Nulo sem renda informada. Zero diria "não sobra nada", que é uma
     * afirmação sobre a vida de alguém que o app não tem como sustentar quando
     * nem sabe quanto essa pessoa ganha.
     */
    private BigDecimal sobraMensal(BigDecimal renda, BigDecimal gastosFixos, BigDecimal parcelas) {
        if (renda.signum() <= 0) return null;
        return escala(renda.subtract(gastosFixos).subtract(parcelas));
    }

    /**
     * O teto diário: o menor entre a sobra real e o que o plano permite,
     * espalhado pelos dias do mês. Nulo entra, nulo sai.
     *
     * <p>O plano limita o que se pretende gastar; a sobra limita o que existe.
     * Vale o mais apertado dos dois. Quando a sobra é negativa ela ganha
     * sozinha — não há plano que autorize gastar dinheiro que não está lá.
     */
    private BigDecimal porDia(BigDecimal sobraMensal, BigDecimal limiteLazer) {
        if (sobraMensal == null) return null;

        BigDecimal base = limiteLazer != null && limiteLazer.compareTo(sobraMensal) < 0
                ? limiteLazer
                : sobraMensal;
        int diasNoMes = YearMonth.now().lengthOfMonth();
        return base.divide(new BigDecimal(diasNoMes), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal gastosFixosMensais(Long idUsuario) {
        return soma(financeIntegrationApi.getGastosFixos(idUsuario).stream()
                .map(GastoFixoResponse::valor));
    }

    /**
     * Reduz a curva ao teto de pontos mantendo o último — ele é o 100%, e
     * perdê-lo tiraria justamente o que a curva promete.
     */
    private List<PontoProgresso> amostrar(List<PontoProgresso> curva) {
        if (curva.size() <= MAX_PONTOS_CURVA) return curva;

        int passo = (int) Math.ceil((double) curva.size() / MAX_PONTOS_CURVA);
        List<PontoProgresso> reduzida = new ArrayList<>();
        for (int i = 0; i < curva.size() - 1; i += passo) {
            reduzida.add(curva.get(i));
        }
        reduzida.add(curva.get(curva.size() - 1));
        return reduzida;
    }

    private static boolean emAberto(ParcelaResponse p) {
        return p.status() != StatusParcela.PAGA && p.status() != StatusParcela.CANCELADA;
    }

    private static BigDecimal soma(Stream<BigDecimal> valores) {
        return valores.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal valor(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal escala(BigDecimal v) {
        return valor(v).setScale(2, RoundingMode.HALF_UP);
    }

    /** Percentual com duas casas. Denominador zero devolve zero, nunca estoura. */
    private static BigDecimal percentual(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) return BigDecimal.ZERO;
        return valor(parte).divide(total, 4, RoundingMode.HALF_UP)
                .multiply(CEM)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
