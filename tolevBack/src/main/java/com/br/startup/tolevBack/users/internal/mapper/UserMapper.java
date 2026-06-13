package com.br.startup.tolevBack.users.internal.mapper;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.entity.UsuarioAssinatura;

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
