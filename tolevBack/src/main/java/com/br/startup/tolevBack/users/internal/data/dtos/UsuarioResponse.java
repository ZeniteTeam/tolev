package com.br.startup.tolevBack.users.internal.data.dtos;

import java.time.LocalDate;

public record UsuarioResponse(
    Long id,
    String nome,
    String genero,
    LocalDate dataNascimento,
    String nomeUsuario,
    String email
) {}
