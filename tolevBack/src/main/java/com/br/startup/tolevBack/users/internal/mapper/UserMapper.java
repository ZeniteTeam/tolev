package com.br.startup.tolevBack.users.internal.mapper;

import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioRequest;
import com.br.startup.tolevBack.users.internal.data.dtos.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.entities.Usuario;
import com.br.startup.tolevBack.users.internal.entities.UsuarioAssinatura;

public class UserMapper {

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getGenero(),
            usuario.getDataNascimento(),
            usuario.getNomeUsuario(),
            usuario.getEmail()
        );
    }

    public static Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
            .nome(request.nome())
            .genero(request.genero())
            .dataNascimento(request.dataNascimento())
            .nomeUsuario(request.nomeUsuario())
            .senha(request.senha())
            .email(request.email())
            .build();
    }

    public static UsuarioAssinaturaResponse toAssinaturaResponse(UsuarioAssinatura usuarioAssinatura) {
        return new UsuarioAssinaturaResponse(
            usuarioAssinatura.getId(),
            usuarioAssinatura.getUsuario().getId(),
            usuarioAssinatura.getAssinatura().getId(),
            usuarioAssinatura.getAssinatura().getModeloAssinatura(),
            usuarioAssinatura.getDataInicio(),
            usuarioAssinatura.getDataFim(),
            usuarioAssinatura.getStatus()
        );
    }
}
