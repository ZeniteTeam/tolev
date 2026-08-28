package com.br.startup.tolevBack.finance.internal.enums;

/**
 * De qual tabela a categoria veio. As duas convivem na mesma lista para o app,
 * mas a transação guarda a FK numa coluna diferente para cada uma.
 */
public enum OrigemCategoria {
    SISTEMA,
    USUARIO;
}
