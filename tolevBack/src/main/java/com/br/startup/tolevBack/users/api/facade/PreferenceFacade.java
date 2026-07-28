package com.br.startup.tolevBack.users.api.facade;

import com.br.startup.tolevBack.users.application.dto.request.PreferenciaFinanceiraRequest;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.application.usecase.commands.UpdatePreferenciaFinanceiraService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetPreferenciaFinanceiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceFacade {

    private final GetPreferenciaFinanceiraService getPreferencia;
    private final UpdatePreferenciaFinanceiraService updatePreferencia;

    public PreferenciaFinanceiraResponse get(Long idUsuario) {
        return getPreferencia.execute(idUsuario);
    }

    public PreferenciaFinanceiraResponse update(Long idUsuario, PreferenciaFinanceiraRequest request) {
        return updatePreferencia.execute(idUsuario, request);
    }
}
