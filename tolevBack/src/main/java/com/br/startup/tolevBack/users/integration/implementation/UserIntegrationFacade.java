package com.br.startup.tolevBack.users.integration.implementation;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.request.UsuarioResendDTO;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.application.usecase.commands.UpdateUserService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetPreferenciaFinanceiraService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetUserByIdService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetUserByReceberEmail;
import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIntegrationFacade implements UserIntegrationApi {

    private final GetUserByIdService getUserById;
    private final UpdateUserService updateUser;
    private final GetPreferenciaFinanceiraService getPreferencia;
    private final GetUserByReceberEmail getUserByReceberEmail;

    @Override
    public UsuarioResponse getUserById(Long id) {
        return getUserById.execute(id);
    }

    @Override
    public UsuarioResponse updateUser(Long id, UsuarioRequest request) {
        return updateUser.execute(id, request);
    }

    @Override
    public PreferenciaFinanceiraResponse getPreferencias(Long idUsuario) {
        return getPreferencia.execute(idUsuario);
    }

    @Override
    public List<UsuarioResendDTO> findByReceberEmail(Boolean ativo) {return getUserByReceberEmail.execute(ativo); }
}
