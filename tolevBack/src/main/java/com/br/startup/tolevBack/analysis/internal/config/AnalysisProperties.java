package com.br.startup.tolevBack.analysis.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Parâmetros do motor de análise, lidos do prefixo {@code analise.*}.
 *
 * <p>Ficam fora do código porque são calibragem de produto, não regra: o limiar
 * do que merece virar recomendação vai mudar quando houver usuário real
 * suficiente para observar o que ajuda e o que só incomoda.
 *
 * @param janelaDias             quanto histórico de transação a análise enxerga
 * @param debounceMinutos        intervalo mínimo entre dois recálculos do mesmo usuário
 * @param ocorrenciasMinimas     dias distintos em que o achado precisa aparecer para virar recomendação
 * @param impactoAnualMinimo     custo anual projetado (R$) abaixo do qual o achado não vira recomendação
 * @param janelaRecorrenciaDias  quanto tempo para trás a recorrência é contada
 * @param cooldownRecriacaoDias  quanto esperar antes de recriar uma recomendação já concluída
 */
@ConfigurationProperties(prefix = "analise")
public record AnalysisProperties(
        Integer janelaDias,
        Integer debounceMinutos,
        Integer ocorrenciasMinimas,
        BigDecimal impactoAnualMinimo,
        Integer janelaRecorrenciaDias,
        Integer cooldownRecriacaoDias
) {
    public AnalysisProperties {
        janelaDias = janelaDias != null ? janelaDias : 180;
        debounceMinutos = debounceMinutos != null ? debounceMinutos : 30;
        ocorrenciasMinimas = ocorrenciasMinimas != null ? ocorrenciasMinimas : 3;
        impactoAnualMinimo = impactoAnualMinimo != null ? impactoAnualMinimo : new BigDecimal("600");
        janelaRecorrenciaDias = janelaRecorrenciaDias != null ? janelaRecorrenciaDias : 30;
        cooldownRecriacaoDias = cooldownRecriacaoDias != null ? cooldownRecriacaoDias : 30;
    }
}
