package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.graphs.application.dto.response.ProgressionGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.ProgressionGraphResponse.ModuloDataPoint;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressaoModuloResponse;
import com.br.startup.tolevBack.progression.integration.api.ProgressionIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetProgressionGraphService {

    private final ProgressionIntegrationApi progressionIntegrationApi;

    public ProgressionGraphResponse execute(Long idUsuario) {
        List<ProgressaoModuloResponse> modulos = progressionIntegrationApi.getProgressaoModulos(idUsuario);

        BigDecimal progressaoMedia = modulos.isEmpty() ? BigDecimal.ZERO
                : modulos.stream()
                        .map(ProgressaoModuloResponse::progressao)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(modulos.size()), 2, RoundingMode.HALF_UP);

        List<ModuloDataPoint> pontos = modulos.stream()
                .map(m -> new ModuloDataPoint(
                        m.idMapaModulo(),
                        m.tipoModulo() != null ? m.tipoModulo().name() : null,
                        m.estiloModulo() != null ? m.estiloModulo().name() : null,
                        m.progressao()
                ))
                .toList();

        return new ProgressionGraphResponse(idUsuario, modulos.size(), progressaoMedia, pontos);
    }
}
