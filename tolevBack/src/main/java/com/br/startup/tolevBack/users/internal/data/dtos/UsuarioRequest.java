package com.br.startup.tolevBack.users.internal.data.dtos;

import java.time.LocalDate;

public record UsuarioRequest(
    String nome,
    String genero,
    LocalDate dataNascimento,
    String nomeUsuario,
    String senha,
    String email
) {}
