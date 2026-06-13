package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.TransactionRequest;
import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import com.br.startup.tolevBack.finance.internal.entity.Vendedor;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import com.br.startup.tolevBack.finance.internal.repository.IVendorRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final IVendorRepository vendorRepository;

    @Transactional
    public TransactionResponse execute(TransactionRequest request) {
        ContaBancaria conta = accountRepository.findById(request.idContaBancaria())
                .orElseThrow(() -> new NotFoundException("Conta não encontrada com id: " + request.idContaBancaria()));

        Vendedor vendedor = null;
        if (request.idVendedor() != null) {
            vendedor = vendorRepository.findById(request.idVendedor())
                    .orElseThrow(() -> new NotFoundException("Vendedor não encontrado com id: " + request.idVendedor()));
        }

        Transacao transacao = Transacao.builder()
                .contaBancaria(conta)
                .vendedor(vendedor)
                .valor(request.valor())
                .dataTransacao(request.dataTransacao())
                .tipo(request.tipo())
                .descricao(request.descricao())
                .parcelado(request.parcelado())
                .totalParcelas(request.totalParcelas())
                .numeroParcela(request.numeroParcela())
                .metodoPagamento(request.metodoPagamento())
                .build();

        return TransactionMapper.toResponse(transactionRepository.save(transacao));
    }
}
