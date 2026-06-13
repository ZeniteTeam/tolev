package com.br.startup.tolevBack.users.application.dto.response;

import java.time.LocalDate;

public record UsuarioResponse(
    Long id,
    String nome,
    String genero,
    LocalDate dataNascimento,
    String nomeUsuario,
    String email
) {}
