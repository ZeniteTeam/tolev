package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.entity.Transacao;

public class TransactionMapper {

    public static TransactionResponse toResponse(Transacao transacao) {
        CategoriaGastoSistema categoriaSistema = transacao.getCategoriaGastoSistema();
        CategoriaGastoUsuario categoriaUsuario = transacao.getCategoriaGastoUsuario();

        return new TransactionResponse(
                transacao.getId(),
                transacao.getIdUsuario(),
                transacao.getContaBancaria() != null ? transacao.getContaBancaria().getId() : null,
                transacao.getVendedor() != null ? transacao.getVendedor().getId() : null,
                transacao.getVendedor() != null ? transacao.getVendedor().getNomeEmpresa() : null,
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getTipo(),
                transacao.getDescricao(),
                transacao.getDescricaoNormalizada(),
                transacao.getParcelado(),
                transacao.getTotalParcelas(),
                transacao.getNumeroParcela(),
                transacao.getMetodoPagamento(),
                categoriaSistema != null ? categoriaSistema.getId() : null,
                categoriaUsuario != null ? categoriaUsuario.getId() : null,
                nomeCategoria(categoriaSistema, categoriaUsuario),
                corCategoria(categoriaSistema, categoriaUsuario)
        );
    }

    private static String nomeCategoria(CategoriaGastoSistema sistema, CategoriaGastoUsuario usuario) {
        if (sistema != null) {
            return sistema.getNome();
        }
        return usuario != null ? usuario.getNome() : null;
    }

    private static String corCategoria(CategoriaGastoSistema sistema, CategoriaGastoUsuario usuario) {
        if (sistema != null) {
            return sistema.getCor();
        }
        return usuario != null ? usuario.getCor() : null;
    }
}
