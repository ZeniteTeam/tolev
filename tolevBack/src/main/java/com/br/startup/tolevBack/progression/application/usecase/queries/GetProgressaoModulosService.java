package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import com.br.startup.tolevBack.progression.internal.mapper.ProgressaoModuloMapper;
import com.br.startup.tolevBack.progression.internal.repository.IModuloProgressaoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProgressaoModulosService {

    private final IModuloProgressaoUsuarioRepository moduloRepository;

    @Transactional(readOnly = true)
    public List<ProgressaoModuloResponse> execute(Long idUsuario) {
        return moduloRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(ProgressaoModuloMapper::toResponse)
                .toList();
    }
}
