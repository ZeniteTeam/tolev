package com.br.startup.tolevBack.users.application.dto.response;

import com.br.startup.tolevBack.users.internal.enums.StatusAssinatura;
import java.time.LocalDate;

public record UsuarioAssinaturaResponse(
    Long id,
    Long idUsuario,
    Long idAssinatura,
    String modeloAssinatura,
    LocalDate dataInicio,
    LocalDate dataFim,
    StatusAssinatura status
) {}
