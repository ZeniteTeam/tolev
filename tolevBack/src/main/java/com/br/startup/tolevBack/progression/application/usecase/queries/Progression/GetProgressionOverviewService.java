package com.br.startup.tolevBack.progression.application.usecase.queries.Progression;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressionOverviewResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IModuloProgressaoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetProgressionOverviewService {

    private final IDividaRepository dividaRepository;
    private final IModuloProgressaoUsuarioRepository moduloRepository;

    @Transactional(readOnly = true)
    public ProgressionOverviewResponse execute(Long idUsuario) {
        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);
        List<ModuloProgressaoUsuario> modulos = moduloRepository.findByIdUsuario(idUsuario);

        int dividasAtivas = (int) dividas.stream().filter(d -> StatusDivida.ATIVA.equals(d.getStatus())).count();
        int dividasAtrasadas = (int) dividas.stream().filter(d -> StatusDivida.ATRASADA.equals(d.getStatus())).count();

        BigDecimal progressaoMedia = modulos.isEmpty() ? BigDecimal.ZERO
                : modulos.stream()
                        .map(ModuloProgressaoUsuario::getProgressao)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(modulos.size()), 2, RoundingMode.HALF_UP);

        return new ProgressionOverviewResponse(
                idUsuario,
                modulos.size(),
                progressaoMedia,
                dividas.size(),
                dividasAtivas,
                dividasAtrasadas
        );
    }
}
