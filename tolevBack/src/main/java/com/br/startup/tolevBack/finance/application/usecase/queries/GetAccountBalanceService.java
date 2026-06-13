package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.AccountBalanceResponse;
import com.br.startup.tolevBack.finance.internal.mapper.AccountMapper;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountBalanceService {

    private final IAccountRepository accountRepository;

    @Transactional(readOnly = true)
    public AccountBalanceResponse execute(Long id) {
        return accountRepository.findById(id)
                .map(AccountMapper::toBalanceResponse)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada com id: " + id));
    }
}
