package com.br.startup.tolevBack.users.application.dto.response;

import com.br.startup.tolevBack.users.internal.enums.ObjetivoPrincipal;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import com.br.startup.tolevBack.users.internal.enums.TipoEmprego;

import java.time.LocalDate;

public record UsuarioResponse(
    Long id,
    String nome,
    String genero,
    LocalDate dataNascimento,
    ObjetivoPrincipal objetivoPrincipal,
    SituacaoFinanceira situacaoFinanceira,
    TipoEmprego ocupacao,
    String nomeUsuario,
    String email
) {}
