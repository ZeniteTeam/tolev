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
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ParcelaResponse;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chance de o usuário atrasar algum pagamento nos próximos meses.
 *
 * <p>Modelo logístico: cada fator entra como termo linear e o somatório passa
 * por 1/(1+e⁻ᶻ). Os coeficientes são calibragem inicial baseada nas faixas de
 * comprometimento de renda usuais no crédito brasileiro, não regressão sobre
 * dados reais — por isso ficam gravados em
 * {@code tb_analise_resultado_variavel.coeficiente} junto da versão do modelo:
 * quando houver base para recalibrar, dá para comparar o antes e o depois.
 */
@Service
public class InadimplenciaAnalyzer implements AnalisadorFinanceiro {

    private static final String MODELO = "LOGISTICO_INADIMPLENCIA";
    private static final String VERSAO = "1.0";

    /** Intercepto: com todos os fatores neutros, a chance base fica ~6%. */
    private static final BigDecimal B0 = new BigDecimal("-2.75");
    private static final BigDecimal B_COMPROMETIMENTO = new BigDecimal("2.40");
    private static final BigDecimal B_ATRASO = new BigDecimal("2.10");
    private static final BigDecimal B_SEM_RESERVA = new BigDecimal("1.35");
    private static final BigDecimal B_DEFICIT = new BigDecimal("1.60");
    private static final BigDecimal B_SITUACAO = new BigDecimal("0.90");

    @Override
    public TipoAnalise tipo() {
        return TipoAnalise.INADIMPLENCIA;
    }

    @Override
    public AnalysisDraft analisar(AnalysisSnapshot s) {
        if (s.dividas().isEmpty()) {
            return null; // sem dívida não há o que inadimplir
        }

        List<VariavelDraft> variaveis = new ArrayList<>();
        List<AchadoDraft> achados = new ArrayList<>();
        List<EntidadeDraft> entidades = new ArrayList<>();

        // Cada fator é normalizado para 0–1 antes de entrar na soma: assim o
        // coeficiente é comparável entre variáveis de unidades diferentes.
        BigDecimal fComprometimento = normalizar(s.comprometimentoPercentual(), new BigDecimal("60"));
        BigDecimal fAtraso = fatorAtraso(s);
        BigDecimal fSemReserva = BigDecimal.ONE.subtract(
                normalizar(s.mesesDeReserva(), new BigDecimal("6")));
        BigDecimal fDeficit = s.taxaPoupanca().signum() < 0
                ? normalizar(s.taxaPoupanca().abs(), new BigDecimal("50"))
                : BigDecimal.ZERO;
        BigDecimal fSituacao = fatorSituacao(s);

        BigDecimal z = B0
                .add(B_COMPROMETIMENTO.multiply(fComprometimento))
                .add(B_ATRASO.multiply(fAtraso))
                .add(B_SEM_RESERVA.multiply(fSemReserva))
                .add(B_DEFICIT.multiply(fDeficit))
                .add(B_SITUACAO.multiply(fSituacao));

        BigDecimal probabilidade = Calculo.logistica(z);

        variaveis.add(termo("COMPROMETIMENTO_RENDA",
                Calculo.formatarPercentual(s.comprometimentoPercentual()) + "%",
                fComprometimento, B_COMPROMETIMENTO, "risco cresce acima de 30%"));
        variaveis.add(termo("ATRASO_ATUAL",
                s.diasAtrasoMaximo() + " dias",
                fAtraso, B_ATRASO, "ideal 0 dias"));
        variaveis.add(termo("AUSENCIA_DE_RESERVA",
                Calculo.formatarPercentual(s.mesesDeReserva()) + " meses",
                fSemReserva, B_SEM_RESERVA, "ideal 6 meses"));
        variaveis.add(termo("DEFICIT_MENSAL",
                Calculo.formatarPercentual(s.taxaPoupanca()) + "%",
                fDeficit, B_DEFICIT, "ideal taxa de poupança positiva"));
        variaveis.add(termo("SITUACAO_DECLARADA",
                s.usuario() != null && s.usuario().situacaoFinanceira() != null
                        ? s.usuario().situacaoFinanceira().name() : "NAO_INFORMADA",
                fSituacao, B_SITUACAO, "ideal EQUILIBRADO ou INVESTINDO"));

        detectarParcelasAtrasadas(s, achados, entidades);
        detectarDividaMaisCara(s, achados, entidades);
        detectarRiscoAlto(s, probabilidade, achados);

        // Score é a saúde do lado do crédito: 100 = nenhuma chance de atrasar.
        BigDecimal score = Calculo.CEM.subtract(probabilidade.multiply(Calculo.CEM))
                .setScale(2, RoundingMode.HALF_UP);
        NivelRisco nivel = nivelPorProbabilidade(probabilidade);

        return new AnalysisDraft(
                tipo(),
                resumo(probabilidade, s),
                relevancia(nivel),
                s.hoje().minusMonths(1),
                s.hoje().plusMonths(3),
                !achados.isEmpty(),
                new ResultadoDraft(
                        classificar(probabilidade),
                        score,
                        probabilidade,
                        Calculo.nota(z),
                        nivel,
                        MODELO,
                        VERSAO,
                        explicacao(z, probabilidade, variaveis)),
                variaveis,
                entidades,
                achados);
    }

    private void detectarParcelasAtrasadas(
            AnalysisSnapshot s, List<AchadoDraft> achados, List<EntidadeDraft> entidades) {

        s.parcelasAtrasadas().forEach((idDivida, parcelas) -> {
            DividaResponse divida = s.dividas().stream()
                    .filter(d -> idDivida.equals(d.id()))
                    .findFirst()
                    .orElse(null);
            if (divida == null || parcelas.isEmpty()) {
                return;
            }

            BigDecimal valorAtrasado = parcelas.stream()
                    .map(p -> Calculo.nz(p.valorTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int diasAtraso = parcelas.stream()
                    .filter(p -> p.dataVencimento() != null)
                    .mapToInt(p -> (int) ChronoUnit.DAYS.between(p.dataVencimento(), s.hoje()))
                    .max()
                    .orElse(0);

            BigDecimal encargos = calcularEncargos(divida, parcelas, s);

            achados.add(new AchadoDraft(
                    RegraAnalise.PARCELA_EM_ATRASO,
                    "DIVIDA", idDivida,
                    "USUARIO", s.idUsuario(),
                    parcelas.size() + " parcela(s) de " + divida.nome() + " em atraso há até "
                            + diasAtraso + " dias",
                    diasAtraso > 30 ? NivelRisco.CRITICO : NivelRisco.ALTO,
                    BigDecimal.valueOf(diasAtraso),
                    Calculo.dinheiro(valorAtrasado),
                    Calculo.dinheiro(encargos),
                    // Encargos correm por dia; anualizar a partir do que já
                    // acumulou em `diasAtraso` estima o custo de não resolver.
                    Calculo.dinheiro(projetarAnual(encargos, diasAtraso)),
                    Map.of(
                            "divida", divida.nome() != null ? divida.nome() : "sua dívida",
                            "parcelasAtrasadas", String.valueOf(parcelas.size()),
                            "valorAtrasado", Calculo.formatarMoeda(valorAtrasado),
                            "diasAtraso", String.valueOf(diasAtraso),
                            "encargos", Calculo.formatarMoeda(encargos))));

            entidades.add(new EntidadeDraft("DIVIDA", idDivida, divida.nome(),
                    Calculo.percentual(valorAtrasado, s.valorEmAtraso())));
        });
    }

    /**
     * Multa é cobrada uma vez sobre a parcela; mora corre proporcional aos dias.
     * Os dois campos são opcionais na dívida, então ausência vira zero em vez de
     * inventar taxa de mercado.
     */
    private BigDecimal calcularEncargos(
            DividaResponse divida, List<ParcelaResponse> parcelas, AnalysisSnapshot s) {

        BigDecimal multaPerc = Calculo.nz(divida.multaAtraso());
        BigDecimal moraMensalPerc = Calculo.nz(divida.jurosMora());
        BigDecimal total = BigDecimal.ZERO;

        for (ParcelaResponse parcela : parcelas) {
            BigDecimal valor = Calculo.nz(parcela.valorTotal());
            if (valor.signum() == 0 || parcela.dataVencimento() == null) {
                continue;
            }
            long dias = ChronoUnit.DAYS.between(parcela.dataVencimento(), s.hoje());
            BigDecimal multa = valor.multiply(multaPerc).divide(Calculo.CEM, 2, RoundingMode.HALF_UP);
            BigDecimal mora = valor.multiply(moraMensalPerc)
                    .divide(Calculo.CEM, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(dias))
                    .divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
            total = total.add(multa).add(mora);
        }
        return total;
    }

    /** A dívida com a maior taxa: a que mais custa manter viva. */
    private void detectarDividaMaisCara(
            AnalysisSnapshot s, List<AchadoDraft> achados, List<EntidadeDraft> entidades) {

        DividaResponse maisCara = s.dividas().stream()
                .filter(d -> Calculo.nz(d.saldo()).signum() > 0)
                .filter(d -> Calculo.nz(d.juros()).signum() > 0)
                .max(Comparator.comparing(d -> Calculo.nz(d.juros())))
                .orElse(null);

        if (maisCara == null) {
            return;
        }

        // Juros ao ano a partir da taxa mensal, em regime composto:
        // (1 + i)¹² - 1 aplicado ao saldo devedor.
        double taxaMensal = Calculo.nz(maisCara.juros()).doubleValue() / 100.0;
        double fatorAnual = Math.pow(1 + taxaMensal, 12) - 1;
        BigDecimal jurosAnuais = Calculo.nz(maisCara.saldo())
                .multiply(BigDecimal.valueOf(fatorAnual))
                .setScale(2, RoundingMode.HALF_UP);

        String metodo = s.preferencias() != null && s.preferencias().metodoQuitacao() != null
                ? s.preferencias().metodoQuitacao().name()
                : "AVALANCHE";

        achados.add(new AchadoDraft(
                RegraAnalise.DIVIDA_JUROS_ALTO,
                "DIVIDA", maisCara.id(),
                "USUARIO", s.idUsuario(),
                maisCara.nome() + " a " + Calculo.formatarPercentual(maisCara.juros())
                        + "% a.m. é a dívida mais cara",
                jurosAnuais.compareTo(new BigDecimal("2000")) > 0 ? NivelRisco.ALTO : NivelRisco.MEDIO,
                Calculo.nz(maisCara.juros()),
                jurosAnuais,
                Calculo.dinheiro(jurosAnuais.divide(Calculo.DOZE, 2, RoundingMode.HALF_UP)),
                jurosAnuais,
                Map.of(
                        "divida", maisCara.nome() != null ? maisCara.nome() : "sua dívida",
                        "juros", Calculo.formatarPercentual(maisCara.juros()),
                        "jurosAnuais", Calculo.formatarMoeda(jurosAnuais),
                        "metodo", metodo)));

        entidades.add(new EntidadeDraft("DIVIDA", maisCara.id(), maisCara.nome(),
                Calculo.percentual(maisCara.saldo(), s.totalDividas())));
    }

    private void detectarRiscoAlto(AnalysisSnapshot s, BigDecimal probabilidade, List<AchadoDraft> achados) {
        if (probabilidade.compareTo(new BigDecimal("0.40")) < 0) {
            return;
        }
        // O custo esperado do risco: a chance de atrasar aplicada ao que se
        // paga por mês. É a perda média se nada mudar.
        BigDecimal custoEsperado = probabilidade.multiply(Calculo.nz(s.comprometimentoMensal()))
                .setScale(2, RoundingMode.HALF_UP);

        achados.add(new AchadoDraft(
                RegraAnalise.RISCO_INADIMPLENCIA_ALTO,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Probabilidade de atraso estimada em "
                        + Calculo.formatarPercentual(probabilidade.multiply(Calculo.CEM)) + "%",
                probabilidade.compareTo(new BigDecimal("0.60")) >= 0 ? NivelRisco.CRITICO : NivelRisco.ALTO,
                Calculo.nota(probabilidade.multiply(Calculo.CEM)),
                custoEsperado,
                custoEsperado,
                Calculo.dinheiro(custoEsperado.multiply(Calculo.DOZE)),
                Map.of(
                        "probabilidade", Calculo.formatarPercentual(probabilidade.multiply(Calculo.CEM)),
                        "comprometimento", Calculo.formatarPercentual(s.comprometimentoPercentual()),
                        "mesesReserva", Calculo.formatarPercentual(s.mesesDeReserva()))));
    }

    /** Atraso satura em 90 dias: aos 90 e aos 200 o risco já é o mesmo (máximo). */
    private BigDecimal fatorAtraso(AnalysisSnapshot s) {
        return normalizar(BigDecimal.valueOf(s.diasAtrasoMaximo()), new BigDecimal("90"));
    }

    private BigDecimal fatorSituacao(AnalysisSnapshot s) {
        if (s.usuario() == null || s.usuario().situacaoFinanceira() == null) {
            return new BigDecimal("0.50"); // sem informação, assume o meio
        }
        SituacaoFinanceira situacao = s.usuario().situacaoFinanceira();
        return switch (situacao) {
            case ENDIVIDADO -> BigDecimal.ONE;
            case NO_LIMITE -> new BigDecimal("0.65");
            case EQUILIBRADO -> new BigDecimal("0.20");
            case INVESTINDO -> BigDecimal.ZERO;
        };
    }

    private BigDecimal normalizar(BigDecimal valor, BigDecimal teto) {
        return Calculo.limitar(Calculo.dividir(valor, teto), BigDecimal.ZERO, BigDecimal.ONE)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal projetarAnual(BigDecimal acumulado, int dias) {
        if (dias <= 0) {
            return acumulado;
        }
        return Calculo.dividir(acumulado, BigDecimal.valueOf(dias))
                .multiply(new BigDecimal("365"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param fator      o x normalizado
     * @param coeficiente o b do modelo — guardado para o resultado ser reproduzível
     */
    private VariavelDraft termo(
            String nome, String valor, BigDecimal fator, BigDecimal coeficiente, String referencia) {
        BigDecimal contribuicao = fator.multiply(coeficiente).setScale(4, RoundingMode.HALF_UP);
        return new VariavelDraft(
                nome,
                valor,
                fator.multiply(Calculo.CEM).setScale(2, RoundingMode.HALF_UP),
                coeficiente,
                contribuicao,
                contribuicao.compareTo(new BigDecimal("0.50")) > 0 ? VariavelDraft.NEGATIVO
                        : contribuicao.signum() > 0 ? VariavelDraft.NEUTRO
                        : VariavelDraft.POSITIVO,
                referencia);
    }

    private NivelRisco nivelPorProbabilidade(BigDecimal p) {
        if (p.compareTo(new BigDecimal("0.60")) >= 0) return NivelRisco.CRITICO;
        if (p.compareTo(new BigDecimal("0.40")) >= 0) return NivelRisco.ALTO;
        if (p.compareTo(new BigDecimal("0.20")) >= 0) return NivelRisco.MEDIO;
        return NivelRisco.BAIXO;
    }

    private String classificar(BigDecimal p) {
        if (p.compareTo(new BigDecimal("0.60")) >= 0) return "MUITO_PROVAVEL";
        if (p.compareTo(new BigDecimal("0.40")) >= 0) return "PROVAVEL";
        if (p.compareTo(new BigDecimal("0.20")) >= 0) return "POSSIVEL";
        return "IMPROVAVEL";
    }

    private String relevancia(NivelRisco nivel) {
        return switch (nivel) {
            case CRITICO, ALTO -> "ALTA";
            case MEDIO -> "MEDIA";
            case BAIXO -> "BAIXA";
        };
    }

    private String resumo(BigDecimal probabilidade, AnalysisSnapshot s) {
        String pct = Calculo.formatarPercentual(probabilidade.multiply(Calculo.CEM));
        if (s.diasAtrasoMaximo() > 0) {
            return "Você tem R$ " + Calculo.formatarMoeda(s.valorEmAtraso())
                    + " em atraso e " + pct + "% de chance de novos atrasos nos próximos meses.";
        }
        return "Chance estimada de atrasar algum pagamento nos próximos meses: " + pct + "%.";
    }

    private String explicacao(BigDecimal z, BigDecimal probabilidade, List<VariavelDraft> variaveis) {
        Map<String, String> termos = new HashMap<>();
        variaveis.forEach(v -> termos.put(v.nome(), "b=" + v.peso() + " × x=" + v.valorFaixa() + "%"));
        return "p = 1/(1+e^-z), z = " + z.setScale(4, RoundingMode.HALF_UP)
                + " → p = " + probabilidade + ". Termos: " + termos;
    }
}
