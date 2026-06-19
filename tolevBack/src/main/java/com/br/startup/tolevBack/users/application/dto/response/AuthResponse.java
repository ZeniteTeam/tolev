package com.br.startup.tolevBack.users.application.dto.response;

public record AuthResponse(
    String token,
    String tipo,
    long expiraEmMs,
    UsuarioResponse usuario
) {}
