package com.br.startup.tolevBack.finance.application.dto.response;

import com.br.startup.tolevBack.finance.internal.enums.OrigemCategoria;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;

/**
 * Uma categoria de gasto para o app, venha ela do catálogo do sistema ou das
 * categorias criadas pelo próprio usuário. {@code origem} diz em qual coluna a
 * transação deve gravar o {@code id}.
 */
public record CategoryResponse(
    Long id,
    OrigemCategoria origem,
    String nome,
    String cor,
    TipoCategoriaGasto tipo
) {}
