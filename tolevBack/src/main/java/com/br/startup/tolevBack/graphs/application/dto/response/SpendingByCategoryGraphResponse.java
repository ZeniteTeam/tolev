package com.br.startup.tolevBack.graphs.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SpendingByCategoryGraphResponse(
        Long idUsuario,
        LocalDate inicio,
        LocalDate fim,
        BigDecimal totalDespesas,
        Integer totalTransacoes,
        Integer transacoesSemCategoria,
        List<CategoriaPonto> pontos
) {
    public record CategoriaPonto(
            Long idCategoria,
            String nome,
            String cor,
            BigDecimal valor,
            BigDecimal percentual
    ) {}
}