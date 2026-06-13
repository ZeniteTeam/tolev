package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;

public class TransactionMapper {

    public static TransactionResponse toResponse(Transacao transacao) {
        return new TransactionResponse(
                transacao.getId(),
                transacao.getContaBancaria() != null ? transacao.getContaBancaria().getId() : null,
                transacao.getVendedor() != null ? transacao.getVendedor().getId() : null,
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getTipo(),
                transacao.getDescricao(),
                transacao.getDescricaoNormalizada(),
                transacao.getParcelado(),
                transacao.getTotalParcelas(),
                transacao.getNumeroParcela(),
                transacao.getMetodoPagamento()
        );
    }
}
