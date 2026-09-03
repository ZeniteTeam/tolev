package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisVariableResponse;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoVariavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * As variáveis da análise mais recente de um tipo: o "por que" da nota.
 *
 * <p>Ordenadas pela contribuição, então quem lê a lista vê primeiro o que mais
 * pesa — que é exatamente o que o usuário precisa mudar.
 */
@Service
@RequiredArgsConstructor
public class GetAnalysisVariablesService {

    private final IAnaliseRepository analiseRepository;
    private final IAnaliseResultadoRepository resultadoRepository;
    private final IAnaliseResultadoVariavelRepository variavelRepository;

    @Transactional(readOnly = true)
    public List<AnalysisVariableResponse> execute(Long idUsuario, TipoAnalise tipo) {
        return analiseRepository.findTopByIdUsuarioAndTipoOrderByDataCriacaoDesc(idUsuario, tipo)
                .flatMap(resultadoRepository::findByAnalise)
                .map(this::variaveisDe)
                .orElseGet(List::of);
    }

    private List<AnalysisVariableResponse> variaveisDe(AnaliseResultado resultado) {
        return variavelRepository.findByAnaliseResultado(resultado)
                .stream()
                .map(v -> new AnalysisVariableResponse(
                        v.getNomeVariavel(),
                        v.getValorVariavel(),
                        v.getValorFaixa(),
                        v.getPeso(),
                        v.getCoeficiente(),
                        v.getImpactoResultado(),
                        v.getFaixaReferencia()))
                .sorted(Comparator.comparing(
                        AnalysisVariableResponse::coeficiente,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}
