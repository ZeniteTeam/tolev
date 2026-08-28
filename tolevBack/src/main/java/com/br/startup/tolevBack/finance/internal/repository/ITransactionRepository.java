package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transacao, Long> {

    /**
     * A listagem mostra vendedor e categoria em cada linha, então eles vêm no
     * mesmo select — sem o grafo seriam três queries extras por transação.
     */
    @EntityGraph(attributePaths = {"vendedor", "categoriaGastoSistema", "categoriaGastoUsuario"})
    List<Transacao> findByIdUsuarioOrderByDataTransacaoDescIdDesc(Long idUsuario);

    /**
     * Recorte por período. A análise olha uma janela fixa de meses e não tem uso
     * para o histórico inteiro — em conta antiga isso seriam milhares de linhas
     * carregadas para calcular a média dos últimos 90 dias.
     */
    @EntityGraph(attributePaths = {"vendedor", "categoriaGastoSistema", "categoriaGastoUsuario"})
    List<Transacao> findByIdUsuarioAndDataTransacaoBetweenOrderByDataTransacaoDescIdDesc(
            Long idUsuario, LocalDate inicio, LocalDate fim);
}
