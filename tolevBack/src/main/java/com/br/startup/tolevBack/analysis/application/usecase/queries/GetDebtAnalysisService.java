package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.DebtAnalysisResponse;
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
public class GetDebtAnalysisService {

    private final IAnaliseRepository analiseRepository;
    private final IAnaliseResultadoRepository resultadoRepository;

    @Transactional(readOnly = true)
    public List<DebtAnalysisResponse> execute(Long idUsuario) {
        return analiseRepository.findByIdUsuarioAndTipo(idUsuario, TipoAnalise.INADIMPLENCIA)
                .stream()
                .map(analise -> {
                    AnaliseResultado resultado = resultadoRepository.findByAnalise(analise).orElse(null);
                    return AnalysisMapper.toDebtResponse(analise, resultado);
                })
                .toList();
    }
}
