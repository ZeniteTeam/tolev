package com.br.startup.tolevBack.finance.integration.implementation;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.application.usecase.queries.*;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceIntegrationFacade implements FinanceIntegrationApi {

    private final GetAccountByIdService getAccountById;
    private final GetAccountsService getAccounts;
    private final GetTransactionsService getTransactions;
    private final GetFinancialOverviewService getFinancialOverview;
    private final GetCategoriesService getCategories;

    @Override
    public AccountResponse getAccountById(Long id) {
        return getAccountById.execute(id);
    }

    @Override
    public List<AccountResponse> getAccountsByUser(Long idUsuario) {
        return getAccounts.execute(idUsuario);
    }

    @Override
    public List<TransactionResponse> getTransactionsByUser(Long idUsuario) {
        return getTransactions.execute(idUsuario);
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserAndPeriod(
            Long idUsuario, LocalDate inicio, LocalDate fim) {
        return getTransactions.execute(idUsuario, inicio, fim);
    }

    @Override
    public FinancialOverviewResponse getFinancialOverview(Long idUsuario) {
        return getFinancialOverview.execute(idUsuario);
    }

    @Override
    public List<CategoryResponse> getCategories(Long idUsuario) {
        return getCategories.execute(idUsuario);
    }
}
