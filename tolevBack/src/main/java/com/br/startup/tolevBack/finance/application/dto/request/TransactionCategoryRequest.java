package com.br.startup.tolevBack.finance.application.dto.request;

/**
 * Recategorização de uma transação já gravada. Exatamente um dos dois ids vem
 * preenchido: a transação aponta para o catálogo do sistema ou para as
 * categorias do usuário, nunca para os dois.
 *
 * <p>Os dois nulos também é um pedido válido — é como se tira a categoria de uma
 * transação classificada por engano.
 */
public record TransactionCategoryRequest(
    Long idCategoriaGastoSistema,
    Long idCategoriaGastoUsuario
) {}
