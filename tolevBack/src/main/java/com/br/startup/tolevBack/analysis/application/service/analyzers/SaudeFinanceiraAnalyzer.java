package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.AchadoDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.EntidadeDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.ResultadoDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.VariavelDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisSnapshot;
import com.br.startup.tolevBack.analysis.application.service.Calculo;
import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.RegraAnalise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saúde financeira: o índice geral que o app mostra como "sua nota".
 *
 * <p>Cinco indicadores ponderados, cada um gravado em
 * {@code tb_analise_resultado_variavel} com o peso e a contribuição que teve.
 * O usuário consegue ver não só a nota, mas o que a está segurando.
 */
@Service
public class SaudeFinanceiraAnalyzer implements AnalisadorFinanceiro {

    private static final String MODELO = "SAUDE_FINANCEIRA_PONDERADA";
    private static final String VERSAO = "1.0";

    /** Teto clássico de comprometimento de renda com dívida. */
    private static final BigDecimal COMPROMETIMENTO_TETO = new BigDecimal("30");
    private static final BigDecimal MESES_RESERVA_IDEAL = new BigDecimal("6");

    private static final BigDecimal PESO_COMPROMETIMENTO = new BigDecimal("0.30");
    private static final BigDecimal PESO_POUPANCA = new BigDecimal("0.20");
    private static final BigDecimal PESO_RESERVA = new BigDecimal("0.20");
    private static final BigDecimal PESO_ENDIVIDAMENTO = new BigDecimal("0.15");
    private static final BigDecimal PESO_PONTUALIDADE = new BigDecimal("0.15");

    @Override
    public TipoAnalise tipo() {
        return TipoAnalise.SAUDE_FINANCEIRA;
    }

    @Override
    public AnalysisDraft analisar(AnalysisSnapshot s) {
        if (!s.temDadosSuficientes()) {
            return null;
        }

        List<VariavelDraft> variaveis = new ArrayList<>();
        List<AchadoDraft> achados = new ArrayList<>();
        List<EntidadeDraft> entidades = new ArrayList<>();

        BigDecimal comprometimento = s.comprometimentoPercentual();
        BigDecimal notaComprometimento = Calculo.notaDecrescente(comprometimento,
                new double[]{10, 20, 30, 40, 60},
                new double[]{100, 88, 68, 40, 0});
        variaveis.add(variavel("COMPROMETIMENTO_RENDA",
                Calculo.formatarPercentual(comprometimento) + "%",
                notaComprometimento, PESO_COMPROMETIMENTO, "ideal até 30% da renda"));

        BigDecimal taxaPoupanca = s.taxaPoupanca();
        BigDecimal notaPoupanca = Calculo.notaCrescente(taxaPoupanca,
                new double[]{-20, 0, 10, 20, 30},
                new double[]{0, 30, 60, 85, 100});
        variaveis.add(variavel("TAXA_POUPANCA",
                Calculo.formatarPercentual(taxaPoupanca) + "%",
                notaPoupanca, PESO_POUPANCA, "ideal a partir de 20%"));

        BigDecimal mesesReserva = s.mesesDeReserva();
        BigDecimal notaReserva = Calculo.notaCrescente(mesesReserva,
                new double[]{0, 1, 3, 6, 12},
                new double[]{0, 30, 65, 95, 100});
        variaveis.add(variavel("RESERVA_EMERGENCIA_MESES",
                Calculo.formatarPercentual(mesesReserva) + " meses",
                notaReserva, PESO_RESERVA, "ideal 6 meses de despesa"));

        // Saldo devedor sobre renda anual: mede o tamanho da dívida, não o
        // aperto do mês. Duas pessoas com a mesma parcela podem dever valores
        // muito diferentes, e quem deve mais demora muito mais para sair.
        BigDecimal endividamento = Calculo.percentual(
                s.totalDividas(), Calculo.nz(s.rendaMensal()).multiply(Calculo.DOZE));
        BigDecimal notaEndividamento = Calculo.notaDecrescente(endividamento,
                new double[]{10, 30, 60, 100, 200},
                new double[]{100, 85, 60, 30, 0});
        variaveis.add(variavel("ENDIVIDAMENTO_SOBRE_RENDA_ANUAL",
                Calculo.formatarPercentual(endividamento) + "%",
                notaEndividamento, PESO_ENDIVIDAMENTO, "ideal até 30% da renda anual"));

        BigDecimal pontualidade = calcularPontualidade(s);
        BigDecimal notaPontualidade = Calculo.notaCrescente(pontualidade,
                new double[]{50, 80, 95, 100},
                new double[]{0, 55, 90, 100});
        variaveis.add(variavel("PONTUALIDADE_PAGAMENTOS",
                Calculo.formatarPercentual(pontualidade) + "%",
                notaPontualidade, PESO_PONTUALIDADE, "ideal 100% em dia"));

        BigDecimal score = variaveis.stream()
                .map(VariavelDraft::coeficiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        detectarComprometimentoAlto(s, comprometimento, achados);
        detectarReservaInsuficiente(s, mesesReserva, achados);
        detectarOrcamentoEstourado(s, achados);

        s.dividas().stream()
                .filter(d -> Calculo.nz(d.saldo()).signum() > 0)
                .forEach(d -> entidades.add(new EntidadeDraft(
                        "DIVIDA", d.id(), d.nome(),
                        Calculo.percentual(d.saldo(), s.totalDividas()))));

        String classificacao = classificar(score);

        return new AnalysisDraft(
                tipo(),
                resumo(score, classificacao, comprometimento),
                relevancia(score),
                s.hoje().minusMonths(1).withDayOfMonth(1),
                s.hoje(),
                !achados.isEmpty(),
                new ResultadoDraft(
                        classificacao,
                        Calculo.nota(score),
                        null,
                        Calculo.nota(score),
                        nivelRisco(score),
                        MODELO,
                        VERSAO,
                        explicacao(variaveis)),
                variaveis,
                entidades,
                achados);
    }

    /**
     * Percentual de parcelas vencidas que foram pagas.
     *
     * <p>Sem nenhuma parcela vencida ainda não há histórico: assume-se 100% em
     * vez de zero, senão quem acabou de contratar a dívida já nasceria com nota
     * péssima de pontualidade.
     */
    private BigDecimal calcularPontualidade(AnalysisSnapshot s) {
        long vencidasNaoPagas = s.parcelasAtrasadas().values().stream()
                .mapToLong(List::size)
                .sum();
        long pagas = s.dividas().stream()
                .filter(d -> d.parcelas() != null)
                .flatMap(d -> d.parcelas().stream())
                .filter(p -> p.dataPagamento() != null)
                .count();

        long totalVencidas = pagas + vencidasNaoPagas;
        if (totalVencidas == 0) {
            return Calculo.CEM;
        }
        return Calculo.percentual(BigDecimal.valueOf(pagas), BigDecimal.valueOf(totalVencidas));
    }

    private void detectarComprometimentoAlto(
            AnalysisSnapshot s, BigDecimal comprometimento, List<AchadoDraft> achados) {

        if (comprometimento.compareTo(COMPROMETIMENTO_TETO) <= 0) {
            return;
        }
        BigDecimal excedente = Calculo.nz(s.comprometimentoMensal())
                .subtract(Calculo.nz(s.rendaMensal()).multiply(COMPROMETIMENTO_TETO).divide(Calculo.CEM,
                        2, java.math.RoundingMode.HALF_UP));

        achados.add(new AchadoDraft(
                RegraAnalise.COMPROMETIMENTO_RENDA_ALTO,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Comprometimento de renda em " + Calculo.formatarPercentual(comprometimento) + "%",
                comprometimento.compareTo(new BigDecimal("50")) > 0 ? NivelRisco.CRITICO : NivelRisco.ALTO,
                Calculo.nota(comprometimento),
                Calculo.dinheiro(excedente),
                Calculo.dinheiro(excedente),
                Calculo.dinheiro(excedente.multiply(Calculo.DOZE)),
                Map.of(
                        "percentual", Calculo.formatarPercentual(comprometimento),
                        "parcelas", Calculo.formatarMoeda(s.comprometimentoMensal()),
                        "renda", Calculo.formatarMoeda(s.rendaMensal()))));
    }

    private void detectarReservaInsuficiente(
            AnalysisSnapshot s, BigDecimal mesesReserva, List<AchadoDraft> achados) {

        if (mesesReserva.compareTo(new BigDecimal("3")) >= 0) {
            return;
        }
        BigDecimal custoMensal = Calculo.nz(s.despesaMensal()).add(Calculo.nz(s.comprometimentoMensal()));
        if (custoMensal.signum() == 0) {
            return;
        }
        BigDecimal metaReserva = custoMensal.multiply(MESES_RESERVA_IDEAL);
        BigDecimal faltante = metaReserva.subtract(s.saldoDisponivel()).max(BigDecimal.ZERO);

        achados.add(new AchadoDraft(
                RegraAnalise.SEM_RESERVA_EMERGENCIA,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Reserva cobre apenas " + Calculo.formatarPercentual(mesesReserva) + " mês(es) de despesa",
                mesesReserva.compareTo(BigDecimal.ONE) < 0 ? NivelRisco.ALTO : NivelRisco.MEDIO,
                Calculo.nota(mesesReserva),
                Calculo.dinheiro(faltante),
                // O custo de não ter reserva é o que se paga por recorrer ao
                // crédito num imprevisto. Uma despesa emergencial no rotativo a
                // ~8% a.m. custa perto de um terço dela mesma ao ano.
                Calculo.dinheiro(custoMensal.multiply(new BigDecimal("0.08"))),
                Calculo.dinheiro(custoMensal.multiply(new BigDecimal("0.96"))),
                Map.of(
                        "meses", Calculo.formatarPercentual(mesesReserva),
                        "saldo", Calculo.formatarMoeda(s.saldoDisponivel()),
                        "despesaMensal", Calculo.formatarMoeda(custoMensal),
                        "metaReserva", Calculo.formatarMoeda(metaReserva))));
    }

    /** O usuário definiu quanto da renda pode ir para dívida; as parcelas passaram disso. */
    private void detectarOrcamentoEstourado(AnalysisSnapshot s, List<AchadoDraft> achados) {
        if (s.preferencias() == null || s.preferencias().percDividas() == null) {
            return;
        }
        BigDecimal percDividas = BigDecimal.valueOf(s.preferencias().percDividas());
        BigDecimal teto = Calculo.nz(s.rendaMensal())
                .multiply(percDividas)
                .divide(Calculo.CEM, 2, java.math.RoundingMode.HALF_UP);

        if (teto.signum() == 0 || Calculo.nz(s.comprometimentoMensal()).compareTo(teto) <= 0) {
            return;
        }
        BigDecimal excedente = s.comprometimentoMensal().subtract(teto);

        achados.add(new AchadoDraft(
                RegraAnalise.ORCAMENTO_DIVIDAS_ESTOURADO,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Parcelas R$ " + Calculo.formatarMoeda(excedente) + " acima do teto definido",
                NivelRisco.MEDIO,
                Calculo.percentual(excedente, teto),
                Calculo.dinheiro(excedente),
                Calculo.dinheiro(excedente),
                Calculo.dinheiro(excedente.multiply(Calculo.DOZE)),
                Map.of(
                        "percDividas", String.valueOf(s.preferencias().percDividas()),
                        "tetoDividas", Calculo.formatarMoeda(teto),
                        "parcelas", Calculo.formatarMoeda(s.comprometimentoMensal()))));
    }

    private VariavelDraft variavel(
            String nome, String valor, BigDecimal nota, BigDecimal peso, String referencia) {
        return new VariavelDraft(
                nome, valor, nota, peso,
                Calculo.nota(nota.multiply(peso)),
                nota.compareTo(new BigDecimal("70")) >= 0 ? VariavelDraft.POSITIVO
                        : nota.compareTo(new BigDecimal("40")) >= 0 ? VariavelDraft.NEUTRO
                        : VariavelDraft.NEGATIVO,
                referencia);
    }

    private String classificar(BigDecimal score) {
        if (score.compareTo(new BigDecimal("85")) >= 0) return "EXCELENTE";
        if (score.compareTo(new BigDecimal("70")) >= 0) return "BOA";
        if (score.compareTo(new BigDecimal("50")) >= 0) return "ATENCAO";
        if (score.compareTo(new BigDecimal("30")) >= 0) return "FRAGIL";
        return "CRITICA";
    }

    private NivelRisco nivelRisco(BigDecimal score) {
        if (score.compareTo(new BigDecimal("70")) >= 0) return NivelRisco.BAIXO;
        if (score.compareTo(new BigDecimal("50")) >= 0) return NivelRisco.MEDIO;
        if (score.compareTo(new BigDecimal("30")) >= 0) return NivelRisco.ALTO;
        return NivelRisco.CRITICO;
    }

    private String relevancia(BigDecimal score) {
        if (score.compareTo(new BigDecimal("50")) < 0) return "ALTA";
        if (score.compareTo(new BigDecimal("70")) < 0) return "MEDIA";
        return "BAIXA";
    }

    private String resumo(BigDecimal score, String classificacao, BigDecimal comprometimento) {
        return "Sua saúde financeira está em " + Calculo.formatarPercentual(score) + "/100 ("
                + classificacao.toLowerCase() + "), com "
                + Calculo.formatarPercentual(comprometimento) + "% da renda comprometida com parcelas.";
    }

    private String explicacao(List<VariavelDraft> variaveis) {
        Map<String, String> pesos = new HashMap<>();
        variaveis.forEach(v -> pesos.put(v.nome(), v.valor() + " → " + v.coeficiente() + " pts"));
        return "Contribuição de cada indicador: " + pesos;
    }
}
