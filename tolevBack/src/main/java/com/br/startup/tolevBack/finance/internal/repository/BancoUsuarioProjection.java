package com.br.startup.tolevBack.finance.internal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * O que as transações do usuário dizem sobre um banco dele.
 *
 * <p>Projeção e não entidade porque isso é um {@code GROUP BY}: carregar as
 * transações para somar em Java traria meses de lançamentos para a memória a fim
 * de produzir cinco números por banco.
 */
public interface BancoUsuarioProjection {

    Long getIdBanco();

    String getNomeBanco();

    String getCodigoBanco();

    long getQuantidadeTransacoes();

    BigDecimal getTotalEntradas();

    BigDecimal getTotalSaidas();

    LocalDate getPrimeiraTransacao();

    LocalDate getUltimaTransacao();
}
