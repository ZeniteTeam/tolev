package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.request.TransactionCategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.CreateTransactionService;
import com.br.startup.tolevBack.finance.application.usecase.commands.UpdateTransactionCategoryService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetTransactionByIdService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetTransactionsService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetTransactionsToClassifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionFacade {

    private final GetTransactionsService getTransactions;
    private final GetTransactionByIdService getTransactionById;
    private final GetTransactionsToClassifyService getTransactionsToClassify;
    private final CreateTransactionService createTransaction;
    private final UpdateTransactionCategoryService updateTransactionCategory;

    public List<TransactionResponse> getAll(Long idUsuario) {
        return getTransactions.execute(idUsuario);
    }

    public TransactionResponse getById(Long id) {
        return getTransactionById.execute(id);
    }

    public List<TransactionResponse> getToClassify(Long idUsuario, int meses, Long idBanco) {
        return getTransactionsToClassify.execute(idUsuario, meses, idBanco);
    }

    public List<TransactionResponse> getToClassify(
            Long idUsuario, LocalDate inicio, LocalDate fim, Long idBanco) {
        return getTransactionsToClassify.execute(idUsuario, inicio, fim, idBanco);
    }

    public TransactionResponse create(TransactionRequest request) {
        return createTransaction.execute(request);
    }

    public TransactionResponse updateCategory(Long id, TransactionCategoryRequest request) {
        return updateTransactionCategory.execute(id, request);
    }
}
