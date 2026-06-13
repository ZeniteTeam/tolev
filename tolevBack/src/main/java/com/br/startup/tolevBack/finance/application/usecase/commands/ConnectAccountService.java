package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.ConnectAccountRequest;
import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.internal.entity.Banco;
import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;
import com.br.startup.tolevBack.finance.internal.enums.StatusConta;
import com.br.startup.tolevBack.finance.internal.mapper.AccountMapper;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import com.br.startup.tolevBack.finance.internal.repository.IBankRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConnectAccountService {

    private final IAccountRepository accountRepository;
    private final IBankRepository bankRepository;

    @Transactional
    public AccountResponse execute(ConnectAccountRequest request) {
        Banco banco = bankRepository.findById(request.idBanco())
                .orElseThrow(() -> new NotFoundException("Banco não encontrado com id: " + request.idBanco()));

        ContaBancaria conta = ContaBancaria.builder()
                .idUsuario(request.idUsuario())
                .banco(banco)
                .numeroConta(request.numeroConta())
                .tipoConta(request.tipoConta())
                .nomeConta(request.nomeConta())
                .moeda(request.moeda())
                .contaConjunta(request.contaConjunta())
                .limiteCredito(request.limiteCredito())
                .agencia(request.agencia())
                .saldoAtual(BigDecimal.ZERO)
                .saldoDisponivel(BigDecimal.ZERO)
                .statusConta(StatusConta.ATIVA)
                .dataAbertura(LocalDate.now())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return AccountMapper.toResponse(accountRepository.save(conta));
    }
}
