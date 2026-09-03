package com.br.startup.tolevBack.analysis.application.service.analyzers;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisSnapshot;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;

/**
 * Um ângulo de análise sobre o mesmo retrato financeiro.
 *
 * <p>Todos os analisadores são injetados como lista pelo orquestrador, então
 * criar um tipo novo de análise é criar um {@code @Service} que implemente esta
 * interface — nenhum outro arquivo muda.
 */
public interface AnalisadorFinanceiro {

    TipoAnalise tipo();

    /**
     * @return o resultado, ou {@code null} quando não há dado suficiente para
     *         uma conclusão honesta — melhor não gerar análise do que gerar uma
     *         baseada em zero.
     */
    AnalysisDraft analisar(AnalysisSnapshot snapshot);
}
