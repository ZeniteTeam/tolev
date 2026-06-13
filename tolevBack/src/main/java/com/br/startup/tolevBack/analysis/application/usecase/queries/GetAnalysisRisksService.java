package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisRiskResponse;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.analysis.internal.mapper.AnalysisMapper;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAnalysisRisksService {

    private final IAnaliseRepository analiseRepository;
    private final IAnaliseResultadoRepository resultadoRepository;

    @Transactional(readOnly = true)
    public List<AnalysisRiskResponse> execute(Long idUsuario) {
        return analiseRepository.findByIdUsuarioAndTipo(idUsuario, TipoAnalise.RISCO)
                .stream()
                .map(analise -> {
                    AnaliseResultado resultado = resultadoRepository.findByAnalise(analise).orElse(null);
                    return AnalysisMapper.toRiskResponse(analise, resultado);
                })
                .toList();
    }
}
