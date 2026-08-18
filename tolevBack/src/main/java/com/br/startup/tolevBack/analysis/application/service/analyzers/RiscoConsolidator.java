package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.EntidadeDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.ResultadoDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft.VariavelDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisSnapshot;
import com.br.startup.tolevBack.analysis.application.service.Calculo;
import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * O risco consolidado, derivado das outras análises.
 *
 * <p>Não implementa {@link AnalisadorFinanceiro} de propósito: os outros
 * analisadores só olham o retrato financeiro, este olha o que eles concluíram.
 * Forçá-lo na mesma interface exigiria que ela carregasse um parâmetro que só
 * uma implementação usa.
 *
 * <p>Não gera achados próprios — os problemas já foram nomeados por quem os
 * detectou; repetir aqui criaria recomendação duplicada.
 */
@Service
public class RiscoConsolidator {

    private static final String MODELO = "RISCO_CONSOLIDADO";
    private static final String VERSAO = "1.0";

    /**
     * Pesos por origem. Inadimplência pesa mais porque é a única que fala de um
     * evento concreto e datado; consumo pesa menos porque é o mais fácil de
     * corrigir sozinho.
     */
    private static final Map<TipoAnalise, BigDecimal> PESOS = Map.of(
            TipoAnalise.INADIMPLENCIA, new BigDecimal("0.40"),
            TipoAnalise.SAUDE_FINANCEIRA, new BigDecimal("0.30"),
            TipoAnalise.PREVISAO, new BigDecimal("0.20"),
            TipoAnalise.CONSUMO, new BigDecimal("0.10"));

    public TipoAnalise tipo() {
        return TipoAnalise.RISCO;
    }

    public AnalysisDraft consolidar(AnalysisSnapshot s, List<AnalysisDraft> analises) {
        List<AnalysisDraft> comResultado = analises.stream()
                .filter(a -> a != null && a.resultado() != null && a.resultado().score() != null)
                .filter(a -> PESOS.containsKey(a.tipo()))
                .toList();

        if (comResultado.isEmpty()) {
            return null;
        }

        List<VariavelDraft> variaveis = new ArrayList<>();
        BigDecimal somaPesos = BigDecimal.ZERO;
        BigDecimal somaPonderada = BigDecimal.ZERO;

        for (AnalysisDraft analise : comResultado) {
            BigDecimal peso = PESOS.get(analise.tipo());
            // Score alto = situação boa; risco é o complemento.
            BigDecimal risco = Calculo.CEM.subtract(analise.resultado().score());
            BigDecimal contribuicao = Calculo.nota(risco.multiply(peso));

            somaPesos = somaPesos.add(peso);
            somaPonderada = somaPonderada.add(contribuicao);

            variaveis.add(new VariavelDraft(
                    "RISCO_" + analise.tipo().name(),
                    analise.resultado().classificacao(),
                    Calculo.nota(risco),
                    peso,
                    contribuicao,
                    risco.compareTo(new BigDecimal("50")) > 0
                            ? VariavelDraft.NEGATIVO : VariavelDraft.POSITIVO,
                    "ideal abaixo de 30"));
        }

        // Renormaliza: se uma das análises não rodou (usuário sem dívida, por
        // exemplo), os pesos restantes não somam 1 e o risco sairia diluído.
        BigDecimal scoreRisco = Calculo.nota(Calculo.dividir(somaPonderada, somaPesos));
        NivelRisco nivel = nivelPorRisco(scoreRisco);

        List<EntidadeDraft> entidades = new ArrayList<>();
        comResultado.forEach(a -> a.entidades().forEach(e -> {
            if (entidades.stream().noneMatch(
                    j -> j.tipoEntidade().equals(e.tipoEntidade())
                            && java.util.Objects.equals(j.idEntidade(), e.idEntidade()))) {
                entidades.add(e);
            }
        }));

        // Achados vêm dos outros: o consolidado empresta a contagem, não os cria.
        long totalAchados = comResultado.stream().mapToLong(a -> a.achados().size()).sum();

        return new AnalysisDraft(
                tipo(),
                resumo(nivel, totalAchados),
                relevancia(nivel),
                s.hoje().minusMonths(1),
                s.hoje(),
                totalAchados > 0,
                new ResultadoDraft(
                        nivel.name(),
                        scoreRisco,
                        null,
                        scoreRisco,
                        nivel,
                        MODELO,
                        VERSAO,
                        explicacao(variaveis)),
                variaveis,
                entidades,
                List.of());
    }

    private NivelRisco nivelPorRisco(BigDecimal risco) {
        if (risco.compareTo(new BigDecimal("70")) >= 0) return NivelRisco.CRITICO;
        if (risco.compareTo(new BigDecimal("50")) >= 0) return NivelRisco.ALTO;
        if (risco.compareTo(new BigDecimal("30")) >= 0) return NivelRisco.MEDIO;
        return NivelRisco.BAIXO;
    }

    private String relevancia(NivelRisco nivel) {
        return switch (nivel) {
            case CRITICO, ALTO -> "ALTA";
            case MEDIO -> "MEDIA";
            case BAIXO -> "BAIXA";
        };
    }

    private String resumo(NivelRisco nivel, long achados) {
        String base = "Risco financeiro geral: " + nivel.name().toLowerCase();
        if (achados > 0) {
            base += " — " + achados + " ponto(s) de atenção identificado(s)";
        }
        return base + ".";
    }

    private String explicacao(List<VariavelDraft> variaveis) {
        StringBuilder texto = new StringBuilder("Risco composto por ");
        for (int i = 0; i < variaveis.size(); i++) {
            VariavelDraft v = variaveis.get(i);
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(v.nome()).append(" (").append(v.valorFaixa())
                    .append(" × ").append(v.peso()).append(")");
        }
        return texto.toString();
    }
}
