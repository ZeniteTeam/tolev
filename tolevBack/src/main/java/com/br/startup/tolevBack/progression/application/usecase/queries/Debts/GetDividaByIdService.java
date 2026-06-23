package com.br.startup.tolevBack.progression.application.usecase.queries.Debts;

import com.br.startup.tolevBack.progression.application.dto.response.DebtProjectionResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.mapper.DebtMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDividaByIdService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional(readOnly = true)
    public DebtProjectionResponse execute(Long id) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + id));
        ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida).orElse(null);
        return DebtMapper.toProjectionResponse(divida, progresso);
    }
}
