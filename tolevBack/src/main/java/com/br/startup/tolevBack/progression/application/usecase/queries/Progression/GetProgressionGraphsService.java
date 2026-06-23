package com.br.startup.tolevBack.progression.application.usecase.queries.Progression;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse.MetaProgressPoint;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IModuloProgressaoUsuarioRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetProgressionGraphsService {

    private final IModuloProgressaoUsuarioRepository moduloRepository;
    private final IMetaRepository metaRepository;
    private final IProgressoMetaRepository progressoMetaRepository;

    @Transactional(readOnly = true)
    public ProgressionGraphsResponse execute(Long idUsuario) {
        List<ModuloProgressaoUsuario> modulos = moduloRepository.findByIdUsuario(idUsuario);

        BigDecimal progressaoMedia = modulos.isEmpty() ? BigDecimal.ZERO
                : modulos.stream()
                        .map(ModuloProgressaoUsuario::getProgressao)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(modulos.size()), 2, RoundingMode.HALF_UP);

        int concluidos = (int) modulos.stream()
                .filter(m -> m.getProgressao() != null && m.getProgressao().compareTo(new BigDecimal("100")) >= 0)
                .count();
        int emProgresso = modulos.size() - concluidos;

        List<Meta> metas = metaRepository.findByIdUsuario(idUsuario);
        List<MetaProgressPoint> metasProgresso = metas.stream()
                .map(meta -> {
                    ProgressoMeta progresso = progressoMetaRepository.findByMeta(meta).orElse(null);
                    return new MetaProgressPoint(
                            meta.getId(),
                            meta.getNomeMeta(),
                            progresso != null ? progresso.getProgresso() : BigDecimal.ZERO,
                            meta.getStatus() != null ? meta.getStatus().name() : null
                    );
                })
                .toList();

        return new ProgressionGraphsResponse(idUsuario, progressaoMedia, concluidos, emProgresso, metasProgresso);
    }
}
