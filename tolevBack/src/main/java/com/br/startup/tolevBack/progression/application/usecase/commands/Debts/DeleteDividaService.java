package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDividaService {

    private final IDividaRepository dividaRepository;

    @Transactional
    public void execute(Long id) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + id));
        dividaRepository.delete(divida);
    }
}
