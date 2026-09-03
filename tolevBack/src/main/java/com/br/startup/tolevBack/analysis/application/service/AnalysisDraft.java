package com.br.startup.tolevBack.analysis.application.service;

import com.br.startup.tolevBack.analysis.internal.enums.NivelRisco;
import com.br.startup.tolevBack.analysis.internal.enums.RegraAnalise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * O que um analisador produz, antes de virar linha no banco.
 *
 * <p>Separar o cálculo da persistência é o que permite reexecutar a análise
 * várias vezes ao dia sobrescrevendo a linha do dia: o analisador não sabe (nem
 * precisa saber) se está criando ou atualizando.
 *
 * @param relevancia texto curto para a lista do app ("ALTA", "MEDIA", "BAIXA")
 * @param acionavel  se há algo que o usuário possa fazer a respeito
 * @param achados    problemas concretos encontrados; viram impacto e, se persistirem, recomendação
 */
public record AnalysisDraft(
        TipoAnalise tipo,
        String resumo,
        String relevancia,
        LocalDate periodoInicio,
        LocalDate periodoFim,
        boolean acionavel,
        ResultadoDraft resultado,
        List<VariavelDraft> variaveis,
        List<EntidadeDraft> entidades,
        List<AchadoDraft> achados
) {

    /**
     * O veredito da análise.
     *
     * @param score            0–100, quanto maior melhor (exceto em RISCO, onde é o próprio risco)
     * @param probabilidade    0–1, só nos modelos que estimam chance de evento
     * @param coeficienteGeral índice composto antes de virar nota
     * @param modeloUtilizado  qual método gerou o número — fica gravado para o resultado ser auditável
     * @param versaoModelo     muda quando os pesos mudam, para não comparar maçã com laranja no gráfico
     */
    public record ResultadoDraft(
            String classificacao,
            BigDecimal score,
            BigDecimal probabilidade,
            BigDecimal coeficienteGeral,
            NivelRisco nivelRisco,
            String modeloUtilizado,
            String versaoModelo,
            String explicacao
    ) {
    }

    /**
     * Uma variável que entrou na conta, com o quanto ela puxou o resultado.
     *
     * <p>É o que torna o score explicável em vez de um número mágico, e o que
     * alimenta o gráfico de "o que está pesando na sua nota".
     *
     * @param valorFaixa      a nota 0–100 que essa variável recebeu
     * @param peso            quanto ela vale no composto (as somas dão 1)
     * @param coeficiente     contribuição efetiva no score: peso × valorFaixa
     * @param impactoResultado POSITIVO, NEGATIVO ou NEUTRO
     * @param faixaReferencia  o alvo, em texto ("ideal até 30%")
     */
    public record VariavelDraft(
            String nome,
            String valor,
            BigDecimal valorFaixa,
            BigDecimal peso,
            BigDecimal coeficiente,
            String impactoResultado,
            String faixaReferencia
    ) {
        public static final String POSITIVO = "POSITIVO";
        public static final String NEGATIVO = "NEGATIVO";
        public static final String NEUTRO = "NEUTRO";
    }

    /** Entidade de outro módulo que a análise levou em conta, e com que peso. */
    public record EntidadeDraft(
            String tipoEntidade,
            Long idEntidade,
            String papelEntidade,
            BigDecimal pesoEntidade
    ) {
    }

    /**
     * Um problema concreto e localizado.
     *
     * @param gravidade      severidade do achado, vira a prioridade da recomendação
     * @param impactoMensal  quanto custa por mês
     * @param impactoAnual   quanto custa por ano se o padrão continuar — é o valor comparado ao limiar
     * @param dados          números já formatados, para preencher os templates de texto
     */
    public record AchadoDraft(
            RegraAnalise regra,
            String entidadeOrigemTipo,
            Long entidadeOrigemId,
            String entidadeImpactadaTipo,
            Long entidadeImpactadaId,
            String descricao,
            NivelRisco gravidade,
            BigDecimal scoreImpacto,
            BigDecimal impactoEstimadoValor,
            BigDecimal impactoMensal,
            BigDecimal impactoAnual,
            Map<String, String> dados
    ) {
    }
}
