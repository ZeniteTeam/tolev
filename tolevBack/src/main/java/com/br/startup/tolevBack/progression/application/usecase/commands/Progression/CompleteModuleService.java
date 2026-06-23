package com.br.startup.tolevBack.progression.application.usecase.commands.Progression;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import com.br.startup.tolevBack.progression.internal.entity.MapaModulo;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import com.br.startup.tolevBack.progression.internal.mapper.ProgressaoModuloMapper;
import com.br.startup.tolevBack.progression.internal.repository.IMapaModuloRepository;
import com.br.startup.tolevBack.progression.internal.repository.IModuloProgressaoUsuarioRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CompleteModuleService {

    private final IMapaModuloRepository mapaModuloRepository;
    private final IModuloProgressaoUsuarioRepository moduloRepository;

    @Transactional
    public ProgressaoModuloResponse execute(Long idMapaModulo, Long idUsuario) {
        MapaModulo mapaModulo = mapaModuloRepository.findById(idMapaModulo)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado com id: " + idMapaModulo));

        ModuloProgressaoUsuario modulo = moduloRepository
                .findByMapaModuloAndIdUsuario(mapaModulo, idUsuario)
                .orElseGet(() -> ModuloProgressaoUsuario.builder()
                        .mapaModulo(mapaModulo)
                        .idUsuario(idUsuario)
                        .progressao(BigDecimal.ZERO)
                        .build());

        modulo.setProgressao(new BigDecimal("100"));
        return ProgressaoModuloMapper.toResponse(moduloRepository.save(modulo));
    }
}
