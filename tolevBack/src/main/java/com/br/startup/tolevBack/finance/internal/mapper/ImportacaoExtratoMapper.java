package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.internal.entity.Banco;
import com.br.startup.tolevBack.finance.internal.entity.ImportacaoExtrato;

public final class ImportacaoExtratoMapper {

    private ImportacaoExtratoMapper() {
    }

    public static ExtratoImportacaoResponse toResponse(ImportacaoExtrato importacao) {
        Banco banco = importacao.getBanco();
        return new ExtratoImportacaoResponse(
                importacao.getId(),
                banco != null ? banco.getId() : null,
                banco != null ? banco.getTitulo() : null,
                importacao.getStatus(),
                importacao.getNomeArquivo(),
                importacao.getErro(),
                importacao.getConfirmadoEm() != null,
                importacao.getCriadoEm(),
                importacao.getConcluidoEm(),
                importacao.getMarcoAnterior(),
                importacao.getDataInicio(),
                importacao.getDataFim(),
                importacao.getQuantidadeLida(),
                importacao.getQuantidadeImportada(),
                importacao.getQuantidadePulada(),
                importacao.getQuantidadeSemCategoria(),
                importacao.getTotalEntradas(),
                importacao.getTotalSaidas());
    }
}
