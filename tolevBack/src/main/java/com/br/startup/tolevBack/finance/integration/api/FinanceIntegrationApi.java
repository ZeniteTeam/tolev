package com.br.startup.tolevBack.finance.integration.api;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;

import java.util.List;

public interface FinanceIntegrationApi {
    AccountResponse getAccountById(Long id);
    List<AccountResponse> getAccountsByUser(Long idUsuario);
    List<TransactionResponse> getTransactionsByUser(Long idUsuario);
    FinancialOverviewResponse getFinancialOverview(Long idUsuario);
}
