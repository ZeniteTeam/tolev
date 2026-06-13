package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.ProgressionStatsResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IMetaRepository;
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
public class GetProgressionStatsService {

    private final IMetaRepository metaRepository;
    private final IDividaRepository dividaRepository;
    private final IModuloProgressaoUsuarioRepository moduloRepository;

    @Transactional(readOnly = true)
    public ProgressionStatsResponse execute(Long idUsuario) {
        List<Meta> metas = metaRepository.findByIdUsuario(idUsuario);
        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);
        List<ModuloProgressaoUsuario> modulos = moduloRepository.findByIdUsuario(idUsuario);

        int metasAtivas = (int) metas.stream().filter(m -> StatusMeta.ATIVA.equals(m.getStatus())).count();
        int metasConcluidas = (int) metas.stream().filter(m -> StatusMeta.CONCLUIDA.equals(m.getStatus())).count();
        int metasCanceladas = (int) metas.stream().filter(m -> StatusMeta.CANCELADA.equals(m.getStatus())).count();

        int dividasAtivas = (int) dividas.stream().filter(d -> StatusDivida.ATIVA.equals(d.getStatus())).count();
        int dividasPagas = (int) dividas.stream().filter(d -> StatusDivida.PAGA.equals(d.getStatus())).count();
        int dividasAtrasadas = (int) dividas.stream().filter(d -> StatusDivida.ATRASADA.equals(d.getStatus())).count();

        BigDecimal valorTotalDividas = dividas.stream()
                .map(Divida::getValorDivida)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal progressaoMedia = modulos.isEmpty() ? BigDecimal.ZERO
                : modulos.stream()
                        .map(ModuloProgressaoUsuario::getProgressao)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(modulos.size()), 2, RoundingMode.HALF_UP);

        return new ProgressionStatsResponse(
                idUsuario,
                metas.size(), metasAtivas, metasConcluidas, metasCanceladas,
                dividas.size(), dividasAtivas, dividasPagas, dividasAtrasadas,
                valorTotalDividas,
                modulos.size(), progressaoMedia
        );
    }
}
