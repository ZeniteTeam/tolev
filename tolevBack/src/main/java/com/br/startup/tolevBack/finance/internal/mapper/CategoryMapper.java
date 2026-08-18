package com.br.startup.tolevBack.finance.internal.mapper;

import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import com.br.startup.tolevBack.finance.internal.enums.OrigemCategoria;

public class CategoryMapper {

    public static CategoryResponse toResponse(CategoriaGastoSistema categoria) {
        return new CategoryResponse(
                categoria.getId(),
                OrigemCategoria.SISTEMA,
                categoria.getNome(),
                categoria.getCor(),
                categoria.getTipo()
        );
    }

    public static CategoryResponse toResponse(CategoriaGastoUsuario categoria) {
        return new CategoryResponse(
                categoria.getId(),
                OrigemCategoria.USUARIO,
                categoria.getNome(),
                categoria.getCor(),
                categoria.getTipo()
        );
    }
}
