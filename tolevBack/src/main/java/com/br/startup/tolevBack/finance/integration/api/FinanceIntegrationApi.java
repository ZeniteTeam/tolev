package com.br.startup.tolevBack.finance.integration.api;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface FinanceIntegrationApi {
    AccountResponse getAccountById(Long id);
    List<AccountResponse> getAccountsByUser(Long idUsuario);
    List<TransactionResponse> getTransactionsByUser(Long idUsuario);

    /** Transações num período fechado — o que a análise usa para média e tendência. */
    List<TransactionResponse> getTransactionsByUserAndPeriod(Long idUsuario, LocalDate inicio, LocalDate fim);

    FinancialOverviewResponse getFinancialOverview(Long idUsuario);

    /** Catálogo de categorias (sistema + usuário), para nomear os gastos na análise. */
    List<CategoryResponse> getCategories(Long idUsuario);
}
