package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisScorePointResponse;
import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Série temporal da nota de um tipo de análise.
 *
 * <p>Como o motor mantém uma análise por tipo por dia, cada linha já é um ponto
 * do gráfico — não há agrupamento a fazer aqui.
 */
@Service
@RequiredArgsConstructor
public class GetAnalysisScoreHistoryService {

    private final IAnaliseRepository analiseRepository;
    private final IAnaliseResultadoRepository resultadoRepository;

    @Transactional(readOnly = true)
    public List<AnalysisScorePointResponse> execute(Long idUsuario, TipoAnalise tipo) {
        return analiseRepository.findByIdUsuarioAndTipoOrderByDataCriacaoAsc(idUsuario, tipo)
                .stream()
                .map(this::paraPonto)
                .filter(Objects::nonNull)
                .toList();
    }

    private AnalysisScorePointResponse paraPonto(Analise analise) {
        AnaliseResultado resultado = resultadoRepository.findByAnalise(analise).orElse(null);
        if (resultado == null || analise.getDataCriacao() == null) {
            return null; // análise sem resultado não é ponto de gráfico
        }
        return new AnalysisScorePointResponse(
                analise.getDataCriacao().toLocalDate(),
                analise.getTipo(),
                resultado.getScore(),
                resultado.getClassificacao(),
                resultado.getNivelRisco(),
                resultado.getProbabilidade());
    }
}
