package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.internal.mapper.AccountMapper;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAccountsService {

    private final IAccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> execute(Long idUsuario) {
        return accountRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }
}
