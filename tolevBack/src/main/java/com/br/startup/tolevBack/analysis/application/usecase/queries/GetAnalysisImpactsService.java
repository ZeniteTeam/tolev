package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisImpactResponse;
import com.br.startup.tolevBack.analysis.internal.config.AnalysisProperties;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseImpacto;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseImpactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * O que está custando dinheiro ao usuário, do mais caro ao mais barato.
 *
 * <p>O mesmo problema é redetectado a cada análise, então a lista traz só a
 * ocorrência mais recente de cada (regra, entidade) — senão o topo do ranking
 * seria três cópias do mesmo achado.
 */
@Service
@RequiredArgsConstructor
public class GetAnalysisImpactsService {

    private final IAnaliseImpactoRepository impactoRepository;
    private final AnalysisProperties properties;

    @Transactional(readOnly = true)
    public List<AnalysisImpactResponse> execute(Long idUsuario) {
        LocalDateTime desde = LocalDateTime.now().minusDays(properties.janelaRecorrenciaDias());

        // A query já vem ordenada por impacto anual desc, então o primeiro de
        // cada chave é o que fica.
        Map<String, AnaliseImpacto> maisRecentePorAchado = new LinkedHashMap<>();
        for (AnaliseImpacto impacto : impactoRepository.buscarRecentes(idUsuario, desde)) {
            maisRecentePorAchado.putIfAbsent(chave(impacto), impacto);
        }

        List<AnalysisImpactResponse> resposta = new ArrayList<>();
        maisRecentePorAchado.values().forEach(i -> resposta.add(paraResposta(i)));
        return resposta;
    }

    private String chave(AnaliseImpacto impacto) {
        return impacto.getRegra() + "#" + impacto.getEntidadeOrigemId();
    }

    private AnalysisImpactResponse paraResposta(AnaliseImpacto i) {
        return new AnalysisImpactResponse(
                i.getId(),
                i.getRegra(),
                i.getTipoImpacto(),
                i.getEntidadeOrigemTipo(),
                i.getEntidadeOrigemId(),
                i.getDescricao(),
                i.getGravidade(),
                i.getScoreImpacto(),
                i.getImpactoEstimadoValor(),
                i.getImpactoTemporalMensal(),
                i.getImpactoTemporalAnual(),
                i.getAnalise() != null ? i.getAnalise().getDataCriacao() : null);
    }
}
