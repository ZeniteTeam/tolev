package com.br.startup.tolevBack.users.integration.api;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.request.UsuarioResendDTO;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;

import java.util.List;

public interface UserIntegrationApi {
    UsuarioResponse getUserById(Long id);
    UsuarioResponse updateUser(Long id, UsuarioRequest request);
    List<UsuarioResendDTO> findByReceberEmail(Boolean ativo);

    /** Preferências financeiras do usuário (método de quitação, orçamento e campos de apoio). */
    PreferenciaFinanceiraResponse getPreferencias(Long idUsuario);
}
