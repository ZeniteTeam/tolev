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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Padrão de consumo: para onde o dinheiro está indo e se isso mudou.
 *
 * <p>Nota alta significa gasto sob controle — diluído entre categorias, estável
 * mês a mês e deixando sobra.
 */
@Service
public class ConsumoAnalyzer implements AnalisadorFinanceiro {

    private static final String MODELO = "CONSUMO_PONDERADO";
    private static final String VERSAO = "1.0";

    /** Categoria acima disso da média já é desvio, não flutuação normal. */
    private static final BigDecimal FATOR_DESVIO = new BigDecimal("1.25");
    /** Excedente mensal abaixo disso não vale o incômodo de virar achado. */
    private static final BigDecimal EXCEDENTE_MINIMO = new BigDecimal("50");
    /** Uma categoria sozinha acima disso do total já é concentração. */
    private static final BigDecimal CONCENTRACAO_LIMITE = new BigDecimal("35");
    private static final int COMPRAS_RECORRENTES = 6;

    @Override
    public TipoAnalise tipo() {
        return TipoAnalise.CONSUMO;
    }

    @Override
    public AnalysisDraft analisar(AnalysisSnapshot s) {
        if (s.transacoes().isEmpty()) {
            return null;
        }

        BigDecimal totalGastoMes = s.gastoPorCategoria().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VariavelDraft> variaveis = new ArrayList<>();
        List<AchadoDraft> achados = new ArrayList<>();
        List<EntidadeDraft> entidades = new ArrayList<>();

        BigDecimal taxaPoupanca = s.taxaPoupanca();
        BigDecimal notaPoupanca = Calculo.notaCrescente(taxaPoupanca,
                new double[]{-20, 0, 10, 20, 30},
                new double[]{0, 30, 60, 85, 100});
        variaveis.add(new VariavelDraft(
                "TAXA_POUPANCA",
                Calculo.formatarPercentual(taxaPoupanca) + "%",
                notaPoupanca, new BigDecimal("0.35"),
                contribuicao(notaPoupanca, "0.35"),
                impacto(notaPoupanca),
                "ideal a partir de 20%"));

        BigDecimal despesaSobreRenda = Calculo.percentual(s.despesaMensal(), s.rendaMensal());
        BigDecimal notaDespesa = Calculo.notaDecrescente(despesaSobreRenda,
                new double[]{50, 70, 85, 100, 120},
                new double[]{100, 80, 55, 30, 0});
        variaveis.add(new VariavelDraft(
                "DESPESA_SOBRE_RENDA",
                Calculo.formatarPercentual(despesaSobreRenda) + "%",
                notaDespesa, new BigDecimal("0.25"),
                contribuicao(notaDespesa, "0.25"),
                impacto(notaDespesa),
                "ideal até 70% da renda"));

        BigDecimal concentracao = maiorFatia(s.gastoPorCategoria(), totalGastoMes);
        BigDecimal notaConcentracao = Calculo.notaDecrescente(concentracao,
                new double[]{25, 35, 50, 70},
                new double[]{100, 75, 45, 10});
        variaveis.add(new VariavelDraft(
                "CONCENTRACAO_CATEGORIA",
                Calculo.formatarPercentual(concentracao) + "%",
                notaConcentracao, new BigDecimal("0.20"),
                contribuicao(notaConcentracao, "0.20"),
                impacto(notaConcentracao),
                "ideal até 35% numa só categoria"));

        BigDecimal variacao = variacaoContraMedia(s);
        BigDecimal notaVariacao = Calculo.notaDecrescente(variacao,
                new double[]{0, 15, 30, 60},
                new double[]{100, 80, 50, 15});
        variaveis.add(new VariavelDraft(
                "VARIACAO_MENSAL",
                Calculo.formatarPercentual(variacao) + "%",
                notaVariacao, new BigDecimal("0.20"),
                contribuicao(notaVariacao, "0.20"),
                impacto(notaVariacao),
                "ideal até 15% acima da média"));

        BigDecimal score = variaveis.stream()
                .map(VariavelDraft::coeficiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        detectarCategoriasEmAlta(s, achados, entidades);
        detectarConcentracao(s, totalGastoMes, concentracao, achados);
        detectarVendedoresRecorrentes(s, achados, entidades);
        detectarDeficit(s, achados);

        String classificacao = classificar(score);

        return new AnalysisDraft(
                tipo(),
                resumo(s, totalGastoMes, classificacao),
                relevancia(score),
                s.hoje().withDayOfMonth(1),
                s.hoje(),
                !achados.isEmpty(),
                new ResultadoDraft(
                        classificacao,
                        Calculo.nota(score),
                        null,
                        Calculo.nota(taxaPoupanca),
                        nivelRisco(score),
                        MODELO,
                        VERSAO,
                        explicacao(variaveis)),
                variaveis,
                entidades,
                achados);
    }

    /** Categorias cujo gasto do mês estourou a própria média histórica. */
    private void detectarCategoriasEmAlta(
            AnalysisSnapshot s, List<AchadoDraft> achados, List<EntidadeDraft> entidades) {

        s.gastoPorCategoria().forEach((categoria, gastoMes) -> {
            BigDecimal media = s.mediaPorCategoria().get(categoria);
            if (media == null || media.signum() == 0) {
                return; // sem histórico não há do que desviar
            }
            if (gastoMes.compareTo(media.multiply(FATOR_DESVIO)) <= 0) {
                return;
            }
            BigDecimal excedente = gastoMes.subtract(media);
            if (excedente.compareTo(EXCEDENTE_MINIMO) < 0) {
                return;
            }

            BigDecimal excedenteAnual = excedente.multiply(Calculo.DOZE);
            BigDecimal desvio = Calculo.percentual(excedente, media);

            achados.add(new AchadoDraft(
                    RegraAnalise.GASTO_CATEGORIA_ACIMA_DA_MEDIA,
                    "CATEGORIA", s.idPorCategoria().get(categoria),
                    "USUARIO", s.idUsuario(),
                    "Gasto com " + categoria + " " + Calculo.formatarPercentual(desvio)
                            + "% acima da média mensal",
                    gravidadePorExcedente(excedente, s.rendaMensal()),
                    Calculo.nota(desvio),
                    Calculo.dinheiro(excedente),
                    Calculo.dinheiro(excedente),
                    Calculo.dinheiro(excedenteAnual),
                    Map.of(
                            "categoria", categoria,
                            "gastoMes", Calculo.formatarMoeda(gastoMes),
                            "mediaMes", Calculo.formatarMoeda(media),
                            "excedente", Calculo.formatarMoeda(excedente),
                            "excedenteAnual", Calculo.formatarMoeda(excedenteAnual))));

            entidades.add(new EntidadeDraft("CATEGORIA", s.idPorCategoria().get(categoria), categoria,
                    Calculo.percentual(gastoMes, s.despesaMesCorrente())));
        });
    }

    private void detectarConcentracao(
            AnalysisSnapshot s, BigDecimal totalGastoMes, BigDecimal concentracao, List<AchadoDraft> achados) {

        if (concentracao.compareTo(CONCENTRACAO_LIMITE) <= 0 || totalGastoMes.signum() == 0) {
            return;
        }
        String categoria = s.gastoPorCategoria().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        if (categoria == null) {
            return;
        }
        BigDecimal valor = s.gastoPorCategoria().get(categoria);

        achados.add(new AchadoDraft(
                RegraAnalise.CONCENTRACAO_EXCESSIVA_CATEGORIA,
                "CATEGORIA", s.idPorCategoria().get(categoria),
                "USUARIO", s.idUsuario(),
                categoria + " concentra " + Calculo.formatarPercentual(concentracao) + "% das despesas",
                concentracao.compareTo(new BigDecimal("50")) > 0 ? NivelRisco.ALTO : NivelRisco.MEDIO,
                Calculo.nota(concentracao),
                Calculo.dinheiro(valor),
                Calculo.dinheiro(valor),
                Calculo.dinheiro(valor.multiply(Calculo.DOZE)),
                Map.of(
                        "categoria", categoria,
                        "percentual", Calculo.formatarPercentual(concentracao),
                        "valor", Calculo.formatarMoeda(valor))));
    }

    /** Muitas compras pequenas no mesmo lugar: o gasto que não parece gasto. */
    private void detectarVendedoresRecorrentes(
            AnalysisSnapshot s, List<AchadoDraft> achados, List<EntidadeDraft> entidades) {

        s.comprasPorVendedor().entrySet().stream()
                .filter(e -> e.getValue() >= COMPRAS_RECORRENTES)
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> {
                    Long idVendedor = e.getKey();
                    BigDecimal total = Calculo.nz(s.gastoPorVendedor().get(idVendedor));
                    if (total.compareTo(EXCEDENTE_MINIMO) < 0) {
                        return;
                    }
                    BigDecimal totalAnual = total.multiply(Calculo.DOZE);
                    String nome = s.nomesVendedor().getOrDefault(idVendedor, "esse estabelecimento");

                    achados.add(new AchadoDraft(
                            RegraAnalise.GASTO_RECORRENTE_VENDEDOR,
                            "VENDEDOR", idVendedor,
                            "USUARIO", s.idUsuario(),
                            e.getValue() + " compras em " + nome + " no mês, somando R$ "
                                    + Calculo.formatarMoeda(total),
                            gravidadePorExcedente(total, s.rendaMensal()),
                            BigDecimal.valueOf(e.getValue()),
                            Calculo.dinheiro(total),
                            Calculo.dinheiro(total),
                            Calculo.dinheiro(totalAnual),
                            Map.of(
                                    "vendedor", nome,
                                    "vezes", String.valueOf(e.getValue()),
                                    "total", Calculo.formatarMoeda(total),
                                    "totalAnual", Calculo.formatarMoeda(totalAnual))));

                    entidades.add(new EntidadeDraft("VENDEDOR", idVendedor, nome,
                            Calculo.percentual(total, s.despesaMesCorrente())));
                });
    }

    private void detectarDeficit(AnalysisSnapshot s, List<AchadoDraft> achados) {
        BigDecimal receita = Calculo.nz(s.rendaMensal());
        BigDecimal despesa = Calculo.nz(s.despesaMensal());
        if (receita.signum() == 0 || despesa.compareTo(receita) <= 0) {
            return;
        }
        BigDecimal deficit = despesa.subtract(receita);

        achados.add(new AchadoDraft(
                RegraAnalise.TAXA_POUPANCA_NEGATIVA,
                "USUARIO", s.idUsuario(),
                "USUARIO", s.idUsuario(),
                "Despesa mensal supera a renda em R$ " + Calculo.formatarMoeda(deficit),
                NivelRisco.CRITICO,
                Calculo.percentual(deficit, receita),
                Calculo.dinheiro(deficit),
                Calculo.dinheiro(deficit),
                Calculo.dinheiro(deficit.multiply(Calculo.DOZE)),
                Map.of(
                        "despesa", Calculo.formatarMoeda(despesa),
                        "receita", Calculo.formatarMoeda(receita),
                        "deficit", Calculo.formatarMoeda(deficit))));
    }

    private BigDecimal maiorFatia(Map<String, BigDecimal> gastos, BigDecimal total) {
        if (total.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return gastos.values().stream()
                .max(Comparator.naturalOrder())
                .map(maior -> Calculo.percentual(maior, total))
                .orElse(BigDecimal.ZERO);
    }

    /** Quanto o mês corrente está acima da média — negativo vira zero (gastar menos não é desvio). */
    private BigDecimal variacaoContraMedia(AnalysisSnapshot s) {
        BigDecimal media = Calculo.nz(s.despesaMensal());
        if (media.signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diferenca = Calculo.nz(s.despesaMesCorrente()).subtract(media);
        return diferenca.signum() <= 0 ? BigDecimal.ZERO : Calculo.percentual(diferenca, media);
    }

    private NivelRisco gravidadePorExcedente(BigDecimal excedente, BigDecimal renda) {
        BigDecimal peso = Calculo.percentual(excedente, renda);
        if (peso.compareTo(new BigDecimal("15")) > 0) return NivelRisco.CRITICO;
        if (peso.compareTo(new BigDecimal("8")) > 0) return NivelRisco.ALTO;
        if (peso.compareTo(new BigDecimal("3")) > 0) return NivelRisco.MEDIO;
        return NivelRisco.BAIXO;
    }

    private BigDecimal contribuicao(BigDecimal nota, String peso) {
        return Calculo.nota(nota.multiply(new BigDecimal(peso)));
    }

    private String impacto(BigDecimal nota) {
        if (nota.compareTo(new BigDecimal("70")) >= 0) return VariavelDraft.POSITIVO;
        if (nota.compareTo(new BigDecimal("40")) >= 0) return VariavelDraft.NEUTRO;
        return VariavelDraft.NEGATIVO;
    }

    private String classificar(BigDecimal score) {
        if (score.compareTo(new BigDecimal("80")) >= 0) return "CONTROLADO";
        if (score.compareTo(new BigDecimal("60")) >= 0) return "EQUILIBRADO";
        if (score.compareTo(new BigDecimal("40")) >= 0) return "ATENCAO";
        return "DESCONTROLADO";
    }

    private NivelRisco nivelRisco(BigDecimal score) {
        if (score.compareTo(new BigDecimal("75")) >= 0) return NivelRisco.BAIXO;
        if (score.compareTo(new BigDecimal("55")) >= 0) return NivelRisco.MEDIO;
        if (score.compareTo(new BigDecimal("35")) >= 0) return NivelRisco.ALTO;
        return NivelRisco.CRITICO;
    }

    private String relevancia(BigDecimal score) {
        if (score.compareTo(new BigDecimal("50")) < 0) return "ALTA";
        if (score.compareTo(new BigDecimal("70")) < 0) return "MEDIA";
        return "BAIXA";
    }

    private String resumo(AnalysisSnapshot s, BigDecimal totalGastoMes, String classificacao) {
        String maiorCategoria = s.gastoPorCategoria().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String base = "Você gastou R$ " + Calculo.formatarMoeda(totalGastoMes) + " neste mês";
        if (maiorCategoria != null) {
            base += ", com " + maiorCategoria + " liderando";
        }
        return base + ". Consumo " + classificacao.toLowerCase() + ".";
    }

    private String explicacao(List<VariavelDraft> variaveis) {
        Map<String, String> textos = new LinkedHashMap<>();
        variaveis.forEach(v -> textos.put(v.nome(), v.valor() + " (nota " + v.valorFaixa() + ")"));
        return "Nota composta por " + textos;
    }
}
