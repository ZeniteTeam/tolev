package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTransactionsService {

    private final ITransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(Long idUsuario) {
        return transactionRepository.findByIdUsuarioOrderByDataTransacaoDescIdDesc(idUsuario)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    /** Mesma listagem, recortada por período (inclusivo nas duas pontas). */
    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(Long idUsuario, LocalDate inicio, LocalDate fim) {
        return transactionRepository
                .findByIdUsuarioAndDataTransacaoBetweenOrderByDataTransacaoDescIdDesc(idUsuario, inicio, fim)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }
}
