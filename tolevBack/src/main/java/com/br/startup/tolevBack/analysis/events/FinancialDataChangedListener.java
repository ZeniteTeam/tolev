package com.br.startup.tolevBack.analysis.events;

import com.br.startup.tolevBack.analysis.application.usecase.commands.GenerateAnalysisService;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Ponto onde o módulo de análise reage ao resto do sistema.
 *
 * <p>{@code AFTER_COMMIT} garante que a análise só enxerga dados já efetivados
 * — sem isso, uma transação que sofresse rollback deixaria uma análise
 * calculada sobre um lançamento que nunca existiu.
 *
 * <p>{@code @Async} tira o cálculo da thread da requisição: o POST do usuário
 * responde na hora e a análise acontece atrás. Como consequência, o listener
 * roda fora da transação original e da exceção não há mais para onde subir — daí
 * o catch: uma análise que falha não pode aparecer como erro num lançamento que
 * foi salvo com sucesso.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FinancialDataChangedListener {

    private final GenerateAnalysisService generateAnalysis;

    @Async("analysisExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoMudarDadosFinanceiros(DadosFinanceirosAlteradosEvent evento) {
        try {
            generateAnalysis.execute(evento.idUsuario(), evento.isAltoImpacto());
        } catch (Exception e) {
            log.error("Falha ao gerar análise do usuário {} após {}",
                    evento.idUsuario(), evento.origem(), e);
        }
    }
}
