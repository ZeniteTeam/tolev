package com.br.startup.tolevBack.finance.application.dto.request;

import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;

/**
 * Uma categoria criada pelo próprio usuário. Só existe para o lado
 * {@code USUARIO} do catálogo — as do sistema são as mesmas para todo mundo e
 * não passam por aqui.
 *
 * @param idUsuario dono da categoria; obrigatório na criação e ignorado na
 *                  edição, onde o dono é o que já está gravado
 * @param cor       hex da identidade visual na grade; opcional
 * @param tipo      de que lado ela vale. Sem tipo a categoria não apareceria em
 *                  lançamento nenhum, porque o formulário filtra por ele
 */
public record CategoryRequest(
    Long idUsuario,
    String nome,
    String cor,
    TipoCategoriaGasto tipo
) {}
