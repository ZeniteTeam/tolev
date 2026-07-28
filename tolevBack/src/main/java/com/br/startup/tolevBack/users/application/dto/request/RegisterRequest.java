package com.br.startup.tolevBack.users.application.dto.request;

import com.br.startup.tolevBack.users.internal.enums.ObjetivoPrincipal;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import com.br.startup.tolevBack.users.internal.enums.TipoEmprego;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterRequest(
    String nome,
    String genero,
    LocalDate dataNascimento,

    // ----- Perfil financeiro coletado no onboarding -----
    ObjetivoPrincipal objetivoPrincipal,
    SituacaoFinanceira situacaoFinanceira,
    TipoEmprego ocupacao,
    BigDecimal rendaMensal,

    @NotBlank(message = "O nome de usuário é obrigatório")
    String nomeUsuario,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    String senha
) {}
