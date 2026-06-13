package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.request.ConnectAccountRequest;
import com.br.startup.tolevBack.finance.application.dto.response.AccountBalanceResponse;
import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.ConnectAccountService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetAccountBalanceService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetAccountByIdService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetAccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final GetAccountsService getAccounts;
    private final GetAccountByIdService getAccountById;
    private final GetAccountBalanceService getAccountBalance;
    private final ConnectAccountService connectAccount;

    public List<AccountResponse> getAll(Long idUsuario) {
        return getAccounts.execute(idUsuario);
    }

    public AccountResponse getById(Long id) {
        return getAccountById.execute(id);
    }

    public AccountBalanceResponse getBalance(Long id) {
        return getAccountBalance.execute(id);
    }

    public AccountResponse connect(ConnectAccountRequest request) {
        return connectAccount.execute(request);
    }
}
