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
        // Quantas despesas da janela ainda pedem categoria: sem categoria nenhuma
        // OU na guarda-chuva "Outros". Mesmo conjunto da lista "para classificar"
        // (GetTransactionsToClassifyService) — "Outros" significa "decido depois",
        // então conta como não classificada, senão o donut e a lista se
        // contradiriam no mesmo card.
        Integer transacoesAClassificar,
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