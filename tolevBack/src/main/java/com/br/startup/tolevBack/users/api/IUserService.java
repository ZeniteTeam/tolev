package com.br.startup.tolevBack.users.api;

import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioRequest;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioAssinaturaResponse;

import java.util.List;

public interface IUserService {
    UsuarioResponse createUser(UsuarioRequest request);
    UsuarioResponse getUserById(Long id);
    UsuarioResponse updateUser(Long id, UsuarioRequest request);
    void deleteUser(Long id);
    List<UsuarioAssinaturaResponse> getUserAssinaturas(Long idUsuario);
}
