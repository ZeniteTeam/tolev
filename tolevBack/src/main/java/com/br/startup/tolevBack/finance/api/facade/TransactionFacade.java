package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.CreateTransactionService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetTransactionByIdService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetTransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionFacade {

    private final GetTransactionsService getTransactions;
    private final GetTransactionByIdService getTransactionById;
    private final CreateTransactionService createTransaction;

    public List<TransactionResponse> getAll(Long idUsuario) {
        return getTransactions.execute(idUsuario);
    }

    public TransactionResponse getById(Long id) {
        return getTransactionById.execute(id);
    }

    public TransactionResponse create(TransactionRequest request) {
        return createTransaction.execute(request);
    }
}
