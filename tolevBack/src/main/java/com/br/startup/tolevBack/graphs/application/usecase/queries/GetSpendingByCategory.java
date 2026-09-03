package com.br.startup.tolevBack.graphs.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.integration.api.FinanceIntegrationApi;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.graphs.application.dto.response.SpendingByCategoryGraphResponse;
import com.br.startup.tolevBack.graphs.application.dto.response.SpendingByCategoryGraphResponse.CategoriaPonto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GetSpendingByCategory {

    private static final String SEM_CATEGORIA = "Sem categoria";
    private static final BigDecimal CEM = new BigDecimal("100");

    private final FinanceIntegrationApi financeIntegrationApi;

    public SpendingByCategoryGraphResponse execute(Long idUsuario, int meses) {
        var end = LocalDate.now();
        var start = YearMonth.from(end).minusMonths(meses - 1).atDay(1);

        var transactions = financeIntegrationApi.getTransactionsByUserAndPeriod(idUsuario, start, end);

        transactions = transactions
                .stream()
                .filter(t -> TipoTransacao.DESPESA.equals(t.tipo()))
                .filter(t -> t.valor() != null)
                .toList();

        Map<String, Acumulador> porCategoria = new LinkedHashMap<>();
        for (TransactionResponse t : transactions) {
            String nome = t.nomeCategoria() != null ? t.nomeCategoria() : SEM_CATEGORIA;
            porCategoria.computeIfAbsent(nome, n -> new Acumulador()).somar(t);
        }

        BigDecimal totalDespesas = transactions.stream()
                .map(TransactionResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoriaPonto> pontos = porCategoria.entrySet().stream()
                .map(e -> new CategoriaPonto(
                        e.getValue().idCategoria,
                        e.getKey(),
                        e.getValue().cor,
                        e.getValue().valor,
                        percentual(e.getValue().valor, totalDespesas)))
                .sorted(Comparator.comparing(CategoriaPonto::valor).reversed())
                .toList();

        int transacoesSemCategoria = (int) transactions.stream()
                .filter(t -> t.idCategoriaGastoSistema() == null && t.idCategoriaGastoUsuario() == null)
                .count();

        return new SpendingByCategoryGraphResponse(
                idUsuario,
                start,
                end,
                totalDespesas,
                transactions.size(),
                transacoesSemCategoria,
                pontos);
    }

    private BigDecimal percentual(BigDecimal parte, BigDecimal total) {
        if (total.signum() == 0) return BigDecimal.ZERO;
        return parte.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(CEM)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Soma de uma categoria enquanto varre as transações. */
    private static final class Acumulador {
        private BigDecimal valor = BigDecimal.ZERO;
        private String cor;
        private Long idCategoria;

        void somar(TransactionResponse t) {
            valor = valor.add(t.valor());
            if (cor == null) cor = t.corCategoria();
            if (idCategoria == null) idCategoria = idDaCategoria(t);
        }

        /**
         * Categoria do sistema tem menor grau de ambiguidade
         */
        private static Long idDaCategoria(TransactionResponse t) {
            if (t.idCategoriaGastoSistema() != null) return t.idCategoriaGastoSistema();
            if (t.idCategoriaGastoUsuario() != null) return -t.idCategoriaGastoUsuario();
            return null;
        }
    }
}
