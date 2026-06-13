package com.br.startup.tolevBack.users.application.dto.request;

import java.time.LocalDate;

public record UsuarioRequest(
    String nome,
    String genero,
    LocalDate dataNascimento,
    String nomeUsuario,
    String senha,
    String email
) {}
