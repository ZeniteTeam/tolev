package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.AddValueToDividaRequest;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AddNewValueToDividaService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional
    public void execute(AddValueToDividaRequest request) {
        Divida divida = dividaRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + request.getId()));

        ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida)
                .orElseGet(() -> {
                    ProgressoDivida novo = new ProgressoDivida();
                    novo.setDivida(divida);
                    novo.setProgresso(BigDecimal.ZERO);
                    return novo;
                });

        BigDecimal atual = progresso.getProgresso() != null ? progresso.getProgresso() : BigDecimal.ZERO;
        progresso.setProgresso(atual.add(request.getValue()));
        progressoDividaRepository.save(progresso);
    }
}
