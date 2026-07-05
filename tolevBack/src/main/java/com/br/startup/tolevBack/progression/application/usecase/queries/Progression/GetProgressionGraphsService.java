package com.br.startup.tolevBack.progression.application.usecase.queries.Progression;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse;
import com.br.startup.tolevBack.progression.application.dto.response.ProgressionGraphsResponse.DividaProgressPoint;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IModuloProgressaoUsuarioRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProgressionGraphsService {

    private final IModuloProgressaoUsuarioRepository moduloRepository;
    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional(readOnly = true)
    public ProgressionGraphsResponse execute(Long idUsuario) {
        List<ModuloProgressaoUsuario> modulos = moduloRepository.findByIdUsuario(idUsuario);

        BigDecimal progressaoMedia = modulos.isEmpty() ? BigDecimal.ZERO
                : modulos.stream()
                        .map(ModuloProgressaoUsuario::getProgressao)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(modulos.size()), 2, RoundingMode.HALF_UP);

        int concluidos = (int) modulos.stream()
                .filter(m -> m.getProgressao() != null && m.getProgressao().compareTo(new BigDecimal("100")) >= 0)
                .count();
        int emProgresso = modulos.size() - concluidos;

        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);
        List<DividaProgressPoint> dividasProgresso = dividas.stream()
                .map(divida -> {
                    ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida).orElse(null);
                    return new DividaProgressPoint(
                            divida.getId(),
                            divida.getNomeDivida(),
                            progresso != null ? progresso.getProgresso() : BigDecimal.ZERO,
                            divida.getStatus() != null ? divida.getStatus().name() : null
                    );
                })
                .toList();

        return new ProgressionGraphsResponse(idUsuario, progressaoMedia, concluidos, emProgresso, dividasProgresso);
    }
}
