package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Despesas do período que pedem uma categoria: as que não têm nenhuma e as
     * que caíram na categoria guarda-chuva do sistema ("Outros").
     *
     * <p>"Outros" entra na fila porque na prática ela significa "ainda não sei"
     * — o formulário de transação exige uma categoria para avançar, então é o
     * que a pessoa escolhe quando quer decidir depois. Se só as sem categoria
     * aparecessem, esse "depois" não teria tela nenhuma.
     *
     * <p>Só despesa: o gráfico de gastos por categoria conta despesa, e uma
     * lista que incluísse receita não fecharia com o percentual mostrado logo
     * acima dela.
     *
     * @param idCatchAll id de "Outros"; passe um id inexistente para trazer só
     *                   as sem categoria nenhuma
     */
    @EntityGraph(attributePaths = {"vendedor", "banco", "categoriaGastoSistema"})
    @Query("""
            select t from Transacao t
            where t.idUsuario = :idUsuario
              and t.tipo = com.br.startup.tolevBack.finance.internal.enums.TipoTransacao.DESPESA
              and t.dataTransacao between :inicio and :fim
              and t.categoriaGastoUsuario is null
              and (t.categoriaGastoSistema is null
                   or t.categoriaGastoSistema.id = :idCatchAll)
            order by t.dataTransacao desc, t.id desc
            """)
    List<Transacao> findParaClassificar(
            @Param("idUsuario") Long idUsuario,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("idCatchAll") Long idCatchAll);

    /** Tudo que saiu de um upload de extrato — usado para desfazer a importação. */
    List<Transacao> findByIdImportacaoExtrato(Long idImportacaoExtrato);

    /**
     * Um resumo por banco do usuário, a partir das transações que carregam a
     * procedência — hoje, as que vieram de extrato importado.
     *
     * <p>Some as transações e não as importações de propósito: desfazer um
     * upload apaga as transações dele, e o card de bancos precisa refletir isso
     * sem depender de recalcular contadores guardados em outra tabela.
     */
    @Query("""
            select b.id            as idBanco,
                   b.titulo        as nomeBanco,
                   b.codigoBanco   as codigoBanco,
                   count(t.id)     as quantidadeTransacoes,
                   coalesce(sum(case when t.tipo = com.br.startup.tolevBack.finance.internal.enums.TipoTransacao.RECEITA
                                     then t.valor else 0 end), 0) as totalEntradas,
                   coalesce(sum(case when t.tipo = com.br.startup.tolevBack.finance.internal.enums.TipoTransacao.DESPESA
                                     then t.valor else 0 end), 0) as totalSaidas,
                   min(t.dataTransacao) as primeiraTransacao,
                   max(t.dataTransacao) as ultimaTransacao
            from Transacao t
            join t.banco b
            where t.idUsuario = :idUsuario
            group by b.id, b.titulo, b.codigoBanco
            order by count(t.id) desc
            """)
    List<BancoUsuarioProjection> resumoPorBanco(@Param("idUsuario") Long idUsuario);
}
