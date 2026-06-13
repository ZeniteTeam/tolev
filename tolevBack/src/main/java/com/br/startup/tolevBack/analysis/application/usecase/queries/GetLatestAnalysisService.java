package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import com.br.startup.tolevBack.analysis.internal.mapper.AnalysisMapper;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetLatestAnalysisService {

    private final IAnaliseRepository analiseRepository;

    @Transactional(readOnly = true)
    public List<AnalysisResponse> execute(Long idUsuario) {
        return Arrays.stream(TipoAnalise.values())
                .map(tipo -> analiseRepository.findByIdUsuarioAndTipo(idUsuario, tipo)
                        .stream()
                        .max((a, b) -> {
                            if (a.getDataCriacao() == null) return -1;
                            if (b.getDataCriacao() == null) return 1;
                            return a.getDataCriacao().compareTo(b.getDataCriacao());
                        }))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AnalysisMapper::toResponse)
                .toList();
    }
}
