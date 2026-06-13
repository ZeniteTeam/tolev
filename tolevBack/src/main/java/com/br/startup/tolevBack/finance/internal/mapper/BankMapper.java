package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.BankResponse;
import com.br.startup.tolevBack.finance.internal.entity.Banco;

public class BankMapper {

    public static BankResponse toResponse(Banco banco) {
        return new BankResponse(
                banco.getId(),
                banco.getTitulo(),
                banco.getAgencia()
        );
    }
}
