package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseImpacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IAnaliseImpactoRepository extends JpaRepository<AnaliseImpacto, Long> {

    List<AnaliseImpacto> findByAnalise(Analise analise);

    void deleteByAnalise(Analise analise);

    /**
     * Em quantas análises distintas esse mesmo achado apareceu na janela.
     *
     * <p>Como cada regra pertence a um único analisador e só existe uma análise
     * por tipo por dia, contar análises distintas é contar dias distintos — sem
     * depender de função de data específica de banco.
     */
    @Query("""
            select count(distinct a.id)
            from AnaliseImpacto i
            join i.analise a
            where a.idUsuario = :idUsuario
              and i.regra = :regra
              and i.entidadeOrigemId = :idEntidade
              and a.dataCriacao >= :desde
            """)
    long contarOcorrencias(
            @Param("idUsuario") Long idUsuario,
            @Param("regra") String regra,
            @Param("idEntidade") Long idEntidade,
            @Param("desde") LocalDateTime desde);

    /**
     * Mesma contagem para achado sem entidade identificável — categoria de
     * transação sem classificação, por exemplo.
     *
     * <p>Consulta separada em vez de {@code :idEntidade is null} na mesma: com o
     * parâmetro nulo o Postgres não consegue inferir o tipo e a query falha em
     * execução, não em compilação.
     */
    @Query("""
            select count(distinct a.id)
            from AnaliseImpacto i
            join i.analise a
            where a.idUsuario = :idUsuario
              and i.regra = :regra
              and i.entidadeOrigemId is null
              and a.dataCriacao >= :desde
            """)
    long contarOcorrenciasSemEntidade(
            @Param("idUsuario") Long idUsuario,
            @Param("regra") String regra,
            @Param("desde") LocalDateTime desde);

    /** Impactos recentes do usuário, do mais caro para o mais barato. */
    @Query("""
            select i
            from AnaliseImpacto i
            join i.analise a
            where a.idUsuario = :idUsuario
              and a.dataCriacao >= :desde
            order by i.impactoTemporalAnual desc nulls last
            """)
    List<AnaliseImpacto> buscarRecentes(
            @Param("idUsuario") Long idUsuario,
            @Param("desde") LocalDateTime desde);
}
