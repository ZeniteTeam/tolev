package com.br.startup.tolevBack.progression.application.usecase.queries;

import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.mapper.DebtMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDebtProjectionService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional(readOnly = true)
    public List<DebtProjectionResponse> execute(Long idUsuario) {
        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);

        return dividas.stream()
                .map(divida -> {
                    ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida).orElse(null);
                    return DebtMapper.toProjectionResponse(divida, progresso);
                })
                .toList();
    }
}
