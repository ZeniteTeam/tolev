package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.AccountBalanceResponse;
import com.br.startup.tolevBack.finance.application.dto.response.AccountResponse;
import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;

public class AccountMapper {

    public static AccountResponse toResponse(ContaBancaria conta) {
        return new AccountResponse(
                conta.getId(),
                conta.getIdUsuario(),
                conta.getBanco() != null ? conta.getBanco().getId() : null,
                conta.getBanco() != null ? conta.getBanco().getTitulo() : null,
                conta.getNumeroConta(),
                conta.getNomeConta(),
                conta.getTipoConta(),
                conta.getMoeda(),
                conta.getSaldoAtual(),
                conta.getSaldoDisponivel(),
                conta.getLimiteCredito(),
                conta.getStatusConta(),
                conta.getDataAbertura()
        );
    }

    public static AccountBalanceResponse toBalanceResponse(ContaBancaria conta) {
        return new AccountBalanceResponse(
                conta.getId(),
                conta.getSaldoAtual(),
                conta.getSaldoDisponivel(),
                conta.getLimiteCredito(),
                conta.getUltimaAtualizacao()
        );
    }
}
