package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.TransactionResponse;
import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import com.br.startup.tolevBack.finance.internal.mapper.TransactionMapper;
import com.br.startup.tolevBack.finance.internal.repository.ICategoriaGastoSistemaRepository;
import com.br.startup.tolevBack.finance.internal.repository.ITransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * As despesas que pedem categoria, na mesma janela que o gráfico de gastos por
 * categoria está mostrando.
 *
 * <p>Mesma janela de propósito: o card diz "3 transações precisam da sua ajuda"
 * e logo abaixo lista quais são. Se as duas contas olhassem períodos
 * diferentes, o número e a lista se contradiriam na mesma tela.
 */
@Service
@RequiredArgsConstructor
public class GetTransactionsToClassifyService {

    /** Acima disso a tela vira um backlog; o resto aparece na leva seguinte. */
    private static final int LIMITE = 50;

    /** Nome semeado da categoria guarda-chuva, em V4__seed_categorias_gasto_sistema. */
    private static final String CATCH_ALL = "Outros";

    /** Nenhuma linha tem esse id, então a fila cai para "só as sem categoria". */
    private static final Long SEM_CATCH_ALL = -1L;

    private final ITransactionRepository transactionRepository;
    private final ICategoriaGastoSistemaRepository categoriaSistemaRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(Long idUsuario, int meses, Long idBanco) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = YearMonth.from(fim).minusMonths(meses - 1L).atDay(1);
        return execute(idUsuario, inicio, fim, idBanco);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(Long idUsuario, LocalDate inicio, LocalDate fim, Long idBanco) {
        return transactionRepository.findParaClassificar(idUsuario, inicio, fim, idCatchAll())
                .stream()
                .map(TransactionMapper::toResponse)
                // O filtro por banco fica aqui e não na query: `null` significa
                // "todos", incluindo os lançamentos manuais, que não têm banco.
                .filter(t -> idBanco == null || idBanco.equals(t.idBanco()))
                .limit(LIMITE)
                .toList();
    }

    /**
     * Sem a categoria guarda-chuva semeada a fila não quebra: volta a ser só as
     * despesas sem categoria nenhuma, que é o comportamento mínimo correto.
     */
    private Long idCatchAll() {
        return categoriaSistemaRepository
                .findByNomeIgnoreCaseAndTipoAndAtivoTrue(CATCH_ALL, TipoCategoriaGasto.DESPESA)
                .map(c -> c.getId())
                .orElse(SEM_CATCH_ALL);
    }
}
