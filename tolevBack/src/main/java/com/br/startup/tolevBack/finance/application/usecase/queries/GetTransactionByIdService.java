package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTransactionByIdService {

    private final ITransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public TransactionResponse execute(Long id) {
        return transactionRepository.findById(id)
                .map(TransactionMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Transação não encontrada com id: " + id));
    }
}
