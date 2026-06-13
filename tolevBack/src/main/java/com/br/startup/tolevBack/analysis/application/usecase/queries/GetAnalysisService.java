package com.br.startup.tolevBack.analysis.application.usecase.queries;

import com.br.startup.tolevBack.analysis.application.dto.response.AnalysisResponse;
import com.br.startup.tolevBack.analysis.internal.mapper.AnalysisMapper;
import com.br.startup.tolevBack.analysis.internal.repository.IAnaliseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAnalysisService {

    private final IAnaliseRepository analiseRepository;

    @Transactional(readOnly = true)
    public List<AnalysisResponse> execute(Long idUsuario) {
        return analiseRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(AnalysisMapper::toResponse)
                .toList();
    }
}
