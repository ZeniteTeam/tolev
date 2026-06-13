package com.br.startup.tolevBack.users.integration.api;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;

public interface UserIntegrationApi {
    UsuarioResponse getUserById(Long id);
    UsuarioResponse updateUser(Long id, UsuarioRequest request);
}
