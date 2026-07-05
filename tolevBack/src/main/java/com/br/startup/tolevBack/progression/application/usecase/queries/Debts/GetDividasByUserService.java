package com.br.startup.tolevBack.progression.application.usecase.queries.Debts;

import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDividasByUserService {

    private final IDividaRepository dividaRepository;

    @Transactional(readOnly = true)
    public List<DividaResponse> execute(Long idUsuario) {
        return dividaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(DividaMapper::toResponse)
                .toList();
    }
}
