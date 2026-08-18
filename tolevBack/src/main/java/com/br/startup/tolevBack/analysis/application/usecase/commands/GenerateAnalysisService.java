package com.br.startup.tolevBack.analysis.application.usecase.commands;

import com.br.startup.tolevBack.analysis.application.service.AnalysisDraft;
import com.br.startup.tolevBack.analysis.application.service.AnalysisPersistenceService;
import com.br.startup.tolevBack.analysis.application.service.AnalysisSnapshot;
import com.br.startup.tolevBack.analysis.application.service.AnalysisSnapshotAssembler;
import com.br.startup.tolevBack.analysis.application.service.RecommendationEngine;
import com.br.startup.tolevBack.analysis.application.service.analyzers.AnalisadorFinanceiro;
import com.br.startup.tolevBack.analysis.application.service.analyzers.RiscoConsolidator;
import com.br.startup.tolevBack.analysis.internal.config.AnalysisProperties;
import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recalcula todas as análises de um usuário e promove os achados persistentes a
 * recomendações.
 *
 * <p>Não recebe "o que mudou" para decidir o que recalcular, e isso é
 * deliberado: as análises se alimentam umas das outras (o risco consolidado
 * depende das quatro), então recalcular só uma deixaria o conjunto inconsistente
 * — o app mostraria uma saúde financeira de hoje ao lado de um risco de terça.
 * A conta inteira é barata perto do custo dessa incoerência.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateAnalysisService {

    private final AnalysisSnapshotAssembler assembler;
    private final List<AnalisadorFinanceiro> analisadores;
    private final RiscoConsolidator riscoConsolidator;
    private final AnalysisPersistenceService persistencia;
    private final RecommendationEngine recommendationEngine;
    private final IAnaliseRepository analiseRepository;
    private final AnalysisProperties properties;

    /**
     * @param forcar ignora o debounce; usado pelos fatos de alto impacto e por
     *               quem pedir a análise explicitamente
     */
    @Transactional
    public List<Analise> execute(Long idUsuario, boolean forcar) {
        if (idUsuario == null) {
            return List.of();
        }
        if (!forcar && recalculadaHaPouco(idUsuario)) {
            log.debug("Análise do usuário {} pulada: recalculada há menos de {} min",
                    idUsuario, properties.debounceMinutos());
            return List.of();
        }

        AnalysisSnapshot snapshot = assembler.montar(idUsuario);
        if (!snapshot.temDadosSuficientes()) {
            log.debug("Usuário {} ainda não tem dados para analisar", idUsuario);
            return List.of();
        }

        List<AnalysisDraft> drafts = new ArrayList<>();
        for (AnalisadorFinanceiro analisador : analisadores) {
            try {
                AnalysisDraft draft = analisador.analisar(snapshot);
                if (draft != null) {
                    drafts.add(draft);
                }
            } catch (Exception e) {
                // Um analisador quebrado não pode levar os outros junto: perder a
                // análise de consumo é ruim, perder a de inadimplência é pior.
                log.error("Analisador {} falhou para o usuário {}",
                        analisador.tipo(), idUsuario, e);
            }
        }

        // O consolidado é o último: ele lê o que os outros concluíram.
        AnalysisDraft risco = riscoConsolidator.consolidar(snapshot, drafts);
        if (risco != null) {
            drafts.add(risco);
        }

        List<Analise> analises = new ArrayList<>();
        for (AnalysisDraft draft : drafts) {
            Analise analise = persistencia.salvar(idUsuario, draft);
            analises.add(analise);

            if (!draft.achados().isEmpty()) {
                // Roda depois de persistir para que a contagem de recorrência já
                // inclua a análise de hoje.
                recommendationEngine.avaliar(idUsuario, analise, draft.achados());
            }
        }

        log.info("Usuário {}: {} análises geradas", idUsuario, analises.size());
        return analises;
    }

    private boolean recalculadaHaPouco(Long idUsuario) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(properties.debounceMinutos());
        return analiseRepository.findTopByIdUsuarioOrderByDataCriacaoDesc(idUsuario)
                .map(Analise::getDataCriacao)
                .filter(Objects::nonNull)
                .filter(data -> data.isAfter(limite))
                .isPresent();
    }
}
