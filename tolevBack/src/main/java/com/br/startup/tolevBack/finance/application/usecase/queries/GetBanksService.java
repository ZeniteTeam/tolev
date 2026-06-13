package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.BankResponse;
import com.br.startup.tolevBack.finance.internal.mapper.BankMapper;
import com.br.startup.tolevBack.finance.internal.repository.IBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBanksService {

    private final IBankRepository bankRepository;

    @Transactional(readOnly = true)
    public List<BankResponse> execute() {
        return bankRepository.findAll()
                .stream()
                .map(BankMapper::toResponse)
                .toList();
    }
}
