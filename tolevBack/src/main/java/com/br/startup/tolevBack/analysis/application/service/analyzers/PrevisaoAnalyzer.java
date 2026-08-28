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
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Para onde a situação caminha: saldo no fim do mês e horizonte de quitação.
 *
 * <p>Projeção linear a partir do comportamento observado. Não tenta prever o
 * imprevisível — assume que o resto do mês se parece com os meses anteriores, o
 * que é justamente a premissa que o usuário precisa ver questionada quando o
 * resultado dá negativo.
 */
@Service
public class PrevisaoAnalyzer implements AnalisadorFinanceiro {

    private static final String MODELO = "PROJECAO_LINEAR";
    private static final String VERSAO = "1.0";

    @Override
    public TipoAnalise tipo() {
        return TipoAnalise.PREVISAO;
    }

    @Override
    public AnalysisDraft analisar(AnalysisSnapshot s) {
        if (!s.temDadosSuficientes()) {
            return null;
        }

        LocalDate hoje = s.hoje();
        LocalDate fimDoMes = YearMonth.from(hoje).atEndOfMonth();
        int diasRestantes = fimDoMes.getDayOfMonth() - hoje.getDayOfMonth();

        BigDecimal despesaDiaria = Calculo.dividir(
                s.despesaMensal(), BigDecimal.valueOf(fimDoMes.getDayOfMonth()));
        BigDecimal despesaAteFimDoMes = despesaDiaria.multiply(BigDecimal.valueOf(diasRestantes))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal parcelasAVencer = parcelasAVencerNoMes(s, hoje, fimDoMes);
        BigDecimal receitaAReceber = receitaAindaEsperada(s);

        BigDecimal saldoProjetado = s.saldoDisponivel()
                .add(receitaAReceber)
                .subtract(despesaAteFimDoMes)
                .subtract(parcelasAVencer)
                .setScale(2, RoundingMode.HALF_UP);

        List<VariavelDraft> variaveis = new ArrayList<>();
        List<AchadoDraft> achados = new ArrayList<>();
        List<EntidadeDraft> entidades = new ArrayList<>();

        variaveis.add(new VariavelDraft("SALDO_ATUAL",
                "R$ " + Calculo.formatarMoeda(s.saldoDisponivel()),
                Calculo.nota(s.saldoDisponivel()), BigDecimal.ONE, Calculo.nota(s.saldoDisponivel()),
                s.saldoDisponivel().signum() > 0 ? VariavelDraft.POSITIVO : VariavelDraft.NEGATIVO,
                "ponto de partida"));
        variaveis.add(new VariavelDraft("RECEITA_A_RECEBER",
                "R$ " + Calculo.formatarMoeda(receitaAReceber),
                Calculo.nota(receitaAReceber), BigDecimal.ONE, Calculo.nota(receitaAReceber),
                VariavelDraft.POSITIVO, "renda mensal ainda não lançada"));
        variaveis.add(new VariavelDraft("DESPESA_PROJETADA",
                "R$ " + Calculo.formatarMoeda(despesaAteFimDoMes),
                Calculo.nota(despesaAteFimDoMes), BigDecimal.ONE,
                Calculo.nota(despesaAteFimDoMes.negate()),
                VariavelDraft.NEGATIVO, diasRestantes + " dias no ritmo atual"));
        variaveis.add(new VariavelDraft("PARCELAS_A_VENCER",
                "R$ " + Calculo.formatarMoeda(parcelasAVencer),
                Calculo.nota(parcelasAVencer), BigDecimal.ONE, Calculo.nota(parcelasAVencer.negate()),
                VariavelDraft.NEGATIVO, "compromissos até " + fimDoMes));
        variaveis.add(new VariavelDraft("SALDO_PROJETADO",
                "R$ " + Calculo.formatarMoeda(saldoProjetado),
                Calculo.nota(saldoProjetado), BigDecimal.ONE, Calculo.nota(saldoProjetado),
                saldoProjetado.signum() >= 0 ? VariavelDraft.POSITIVO : VariavelDraft.NEGATIVO,
                "ideal positivo"));

        Integer mesesParaQuitar = estimarMesesParaQuitar(s);
        if (mesesParaQuitar != null) {
            variaveis.add(new VariavelDraft("MESES_ATE_QUITACAO",
                    mesesParaQuitar + " meses",
                    BigDecimal.valueOf(mesesParaQuitar), BigDecimal.ONE,
                    BigDecimal.valueOf(mesesParaQuitar),
                    VariavelDraft.NEUTRO, "no ritmo de pagamento atual"));
        }

        detectarSaldoNegativo(s, saldoProjetado, despesaAteFimDoMes.add(parcelasAVencer), achados);
        detectarOportunidadeDeAporte(s, achados, entidades);

        BigDecimal score = notaDoSaldo(saldoProjetado, s.despesaMensal());

        return new AnalysisDraft(
                tipo(),
                resumo(saldoProjetado, mesesParaQuitar),
                saldoProjetado.signum() < 0 ? "ALTA" : "MEDIA",
                hoje,
                fimDoMes,
                !achados.isEmpty(),
                new ResultadoDraft(
                        saldoProjetado.signum() >= 0 ? "POSITIVO" : "NEGATIVO",
                        score,
                        null,
                        saldoProjetado,
                        nivelRisco(score),
                        MODELO,
                        VERSAO,
                        "Saldo " + Calculo.formatarMoeda(s.saldoDisponivel())
                                + " + receita " + Calculo.formatarMoeda(receitaAReceber)
                                + " - despesa " + Calculo.formatarMoeda(despesaAteFimDoMes)
                                + " - parcelas " + Calculo.formatarMoeda(parcelasAVencer)
                                + " = " + Calculo.formatarMoeda(saldoProjetado)),
                variaveis,
                entidades,
                achados);
    }

    private BigDecimal parcelasAVencerNoMes(AnalysisSnapshot s, LocalDate hoje, LocalDate fimDoMes) {
        return s.dividas().stream()
                .filter(d -> d.parcelas() != null)
                .flatMap(d -> d.parcelas().stream())
                .filter(p -> !StatusParcela.PAGA.equals(p.status())
                        && !StatusParcela.CANCELADA.equals(p.status()))
                .filter(p -> p.dataVencimento() != null
                        && !p.dataVencimento().isBefore(hoje)
                        && !p.dataVencimento().isAfter(fimDoMes))
                .map(ParcelaResponse::valorTotal)
                .map(Calculo::nz)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Quanto da renda do mês ainda não apareceu como transação. Nunca negativo:
     * quem já recebeu mais do que declara não "deve" receita ao mês.
     */
    private BigDecimal receitaAindaEsperada(AnalysisSnapshot s) {
        return Calculo.nz(s.rendaMensal())
                .subtract(Calculo.nz(s.receitaMesCorrente()))
                .max(BigDecimal.ZERO);
    }

    /**
     * Em quantos meses as dívidas acabam no ritmo atual de pagamento.
     * {@code null} quando não há dívida ou não há pagamento algum — projetar
     * "infinito" não ajudaria ninguém.
     */
    private Integer estimarMesesParaQuitar(AnalysisSnapshot s) {
        BigDecimal saldoTotal = Calculo.nz(s.totalDividas());
        if (saldoTotal.signum() <= 0) {
            return null;
        }
        BigDecimal pagamentoMensal = Calculo.nz(s.comprometimentoMensal())
                .add(s.preferencias() != null ? Calculo.nz(s.preferencias().aporteExtraMensal()) : BigDecimal.ZERO);
        if (pagamentoMensal.signum() <= 0) {
            return null;
        }
        return Calculo.dividir(saldoTotal, pagamentoMensal)
                .setScale(0, RoundingMode.CEILING)
                .intValue();
    }

    private void detectarSaldoNegativo(
            AnalysisSnapshot s, BigDecimal saldoProjetado, BigDecimal compromissos, List<AchadoDraft> achados) {

        if (saldoProjetado.signum() >= 0) {
            return;
        }
        BigDecimal falta = saldoProjetado.abs();

        achados.add(new AchadoDraft(
                RegraAnalise.SALDO_PROJETADO_NEGATIVO,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Projeção de fechar o mês em R$ -" + Calculo.formatarMoeda(falta),
                falta.compareTo(Calculo.nz(s.rendaMensal()).multiply(new BigDecimal("0.2"))) > 0
                        ? NivelRisco.CRITICO : NivelRisco.ALTO,
                Calculo.percentual(falta, s.rendaMensal()),
                Calculo.dinheiro(falta),
                Calculo.dinheiro(falta),
                Calculo.dinheiro(falta.multiply(Calculo.DOZE)),
                Map.of(
                        "saldoAtual", Calculo.formatarMoeda(s.saldoDisponivel()),
                        "compromissos", Calculo.formatarMoeda(compromissos),
                        "saldoProjetado", "-" + Calculo.formatarMoeda(falta))));
    }

    /**
     * Sobrando dinheiro no mês, aponta para onde ele rende mais: a dívida que o
     * método de quitação escolhido priorizaria.
     */
    private void detectarOportunidadeDeAporte(
            AnalysisSnapshot s, List<AchadoDraft> achados, List<EntidadeDraft> entidades) {

        BigDecimal sobra = s.sobraMensal();
        if (sobra.compareTo(new BigDecimal("100")) < 0 || s.dividas().isEmpty()) {
            return;
        }

        DividaResponse alvo = escolherAlvo(s);
        if (alvo == null) {
            return;
        }

        // Metade da sobra: sugerir tudo ignoraria que sobra também vira reserva.
        BigDecimal aporte = sobra.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

        // Cada real amortizado hoje deixa de render juros pelo prazo restante.
        double taxaMensal = Calculo.nz(alvo.juros()).doubleValue() / 100.0;
        int prazoRestante = prazoRestante(alvo);
        double economia = aporte.doubleValue() * (Math.pow(1 + taxaMensal, prazoRestante) - 1);
        BigDecimal economiaJuros = BigDecimal.valueOf(economia).setScale(2, RoundingMode.HALF_UP);

        if (economiaJuros.signum() <= 0) {
            return;
        }

        achados.add(new AchadoDraft(
                RegraAnalise.APORTE_EXTRA_ACELERA_QUITACAO,
                "DIVIDA", alvo.id(),
                "USUARIO", s.idUsuario(),
                "Sobra de R$ " + Calculo.formatarMoeda(sobra) + " pode antecipar a quitação de "
                        + alvo.nome(),
                NivelRisco.BAIXO,
                economiaJuros,
                economiaJuros,
                Calculo.dinheiro(economiaJuros.divide(Calculo.DOZE, 2, RoundingMode.HALF_UP)),
                economiaJuros,
                Map.of(
                        "aporte", Calculo.formatarMoeda(aporte),
                        "sobra", Calculo.formatarMoeda(sobra),
                        "divida", alvo.nome() != null ? alvo.nome() : "sua dívida",
                        "economiaJuros", Calculo.formatarMoeda(economiaJuros))));

        entidades.add(new EntidadeDraft("DIVIDA", alvo.id(), alvo.nome(),
                Calculo.percentual(alvo.saldo(), s.totalDividas())));
    }

    /** Respeita o método de quitação que o usuário escolheu nas preferências. */
    private DividaResponse escolherAlvo(AnalysisSnapshot s) {
        List<DividaResponse> ativas = s.dividas().stream()
                .filter(d -> Calculo.nz(d.saldo()).signum() > 0)
                .toList();
        if (ativas.isEmpty()) {
            return null;
        }
        var metodo = s.preferencias() != null ? s.preferencias().metodoQuitacao() : null;
        if (metodo == null) {
            return ativas.stream().max(Comparator.comparing(d -> Calculo.nz(d.juros()))).orElse(null);
        }
        return switch (metodo) {
            // Avalanche: maior juros primeiro, menor custo total.
            case AVALANCHE -> ativas.stream()
                    .max(Comparator.comparing(d -> Calculo.nz(d.juros()))).orElse(null);
            // Snowball: menor saldo primeiro, vitória rápida.
            case SNOWBALL -> ativas.stream()
                    .min(Comparator.comparing(d -> Calculo.nz(d.saldo()))).orElse(null);
            // Tsunami: a que mais pesa emocionalmente.
            case TSUNAMI -> ativas.stream()
                    .max(Comparator.comparing(d -> d.pesoEmocional() != null ? d.pesoEmocional() : 0))
                    .orElse(null);
        };
    }

    private int prazoRestante(DividaResponse divida) {
        if (divida.parcelas() == null) {
            return divida.quantidadeParcelas() != null ? divida.quantidadeParcelas() : 12;
        }
        long pendentes = divida.parcelas().stream()
                .filter(p -> !StatusParcela.PAGA.equals(p.status())
                        && !StatusParcela.CANCELADA.equals(p.status()))
                .count();
        return pendentes > 0 ? (int) pendentes : 12;
    }

    /** Saldo projetado vira nota pela folga que ele representa sobre a despesa mensal. */
    private BigDecimal notaDoSaldo(BigDecimal saldoProjetado, BigDecimal despesaMensal) {
        if (Calculo.nz(despesaMensal).signum() == 0) {
            return saldoProjetado.signum() >= 0 ? new BigDecimal("70") : new BigDecimal("30");
        }
        BigDecimal folga = Calculo.dividir(saldoProjetado, despesaMensal);
        return Calculo.notaCrescente(folga,
                new double[]{-1, 0, 0.5, 1, 3},
                new double[]{0, 35, 60, 80, 100});
    }

    private NivelRisco nivelRisco(BigDecimal score) {
        if (score.compareTo(new BigDecimal("70")) >= 0) return NivelRisco.BAIXO;
        if (score.compareTo(new BigDecimal("50")) >= 0) return NivelRisco.MEDIO;
        if (score.compareTo(new BigDecimal("30")) >= 0) return NivelRisco.ALTO;
        return NivelRisco.CRITICO;
    }

    private String resumo(BigDecimal saldoProjetado, Integer mesesParaQuitar) {
        String base = saldoProjetado.signum() >= 0
                ? "Seu mês deve fechar com R$ " + Calculo.formatarMoeda(saldoProjetado) + " em conta"
                : "Seu mês deve fechar R$ " + Calculo.formatarMoeda(saldoProjetado.abs()) + " no negativo";
        if (mesesParaQuitar != null) {
            base += ". No ritmo atual, suas dívidas acabam em " + mesesParaQuitar + " meses";
        }
        return base + ".";
    }
}
