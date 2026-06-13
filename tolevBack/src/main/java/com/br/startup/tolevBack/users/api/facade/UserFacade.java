package com.br.startup.tolevBack.users.api.facade;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.application.usecase.commands.CreateUserService;
import com.br.startup.tolevBack.users.application.usecase.commands.DeleteUserService;
import com.br.startup.tolevBack.users.application.usecase.commands.UpdateUserService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetUserAssinaturasService;
import com.br.startup.tolevBack.users.application.usecase.queries.GetUserByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final CreateUserService createUser;
    private final GetUserByIdService getUserById;
    private final UpdateUserService updateUser;
    private final DeleteUserService deleteUser;
    private final GetUserAssinaturasService getUserAssinaturas;

    public UsuarioResponse create(UsuarioRequest request) {
        return createUser.execute(request);
    }

    public UsuarioResponse getById(Long id) {
        return getUserById.execute(id);
    }

    public UsuarioResponse update(Long id, UsuarioRequest request) {
        return updateUser.execute(id, request);
    }

    public void delete(Long id) {
        deleteUser.execute(id);
    }

    public List<UsuarioAssinaturaResponse> getAssinaturas(Long idUsuario) {
        return getUserAssinaturas.execute(idUsuario);
    }
}
