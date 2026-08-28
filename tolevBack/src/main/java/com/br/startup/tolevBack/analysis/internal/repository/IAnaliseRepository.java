package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAnaliseRepository extends JpaRepository<Analise, Long> {
    List<Analise> findByIdUsuario(Long idUsuario);
    List<Analise> findByIdUsuarioAndTipo(Long idUsuario, TipoAnalise tipo);
    Optional<Analise> findTopByIdUsuarioOrderByDataCriacaoDesc(Long idUsuario);

    /**
     * A análise do dia, se já existir. Recálculo dentro do mesmo dia sobrescreve
     * essa linha em vez de inserir outra — é o que mantém o gráfico de evolução
     * com um ponto por dia.
     */
    Optional<Analise> findFirstByIdUsuarioAndTipoAndDataCriacaoBetween(
            Long idUsuario, TipoAnalise tipo, LocalDateTime inicio, LocalDateTime fim);

    /** Série temporal de um tipo, na ordem em que o gráfico desenha. */
    List<Analise> findByIdUsuarioAndTipoOrderByDataCriacaoAsc(Long idUsuario, TipoAnalise tipo);

    Optional<Analise> findTopByIdUsuarioAndTipoOrderByDataCriacaoDesc(Long idUsuario, TipoAnalise tipo);
}
