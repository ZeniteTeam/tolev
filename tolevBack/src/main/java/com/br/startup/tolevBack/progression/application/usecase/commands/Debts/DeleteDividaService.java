package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.shared.events.DadosFinanceirosAlteradosEvent;
import com.br.startup.tolevBack.shared.events.OrigemAlteracao;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDividaService {

    private final IDividaRepository dividaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(Long id) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + id));
        Long idUsuario = divida.getIdUsuario();
        dividaRepository.delete(divida);

        // Sem o id da dívida: ela não existe mais depois do commit, e é só
        // depois do commit que o evento é entregue.
        eventPublisher.publishEvent(DadosFinanceirosAlteradosEvent.de(
                idUsuario, OrigemAlteracao.DIVIDA_REMOVIDA));
    }
}
