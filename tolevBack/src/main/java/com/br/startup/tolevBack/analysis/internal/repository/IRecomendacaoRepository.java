package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.startup.tolevBack.analysis.internal.enums.StatusRecomendacao;

import java.util.List;

@Repository
public interface IRecomendacaoRepository extends JpaRepository<Recomendacao, Long> {
    List<Recomendacao> findByIdUsuario(Long idUsuario);

    /**
     * Recomendações já existentes para essa regra, em qualquer status.
     *
     * <p>O status importa na decisão de recriar: PENDENTE/ACEITA já estão na
     * frente do usuário, IGNORADA foi recusada e CONCLUIDA só volta depois de
     * um tempo — quem decide é o motor, então a consulta traz todas.
     */
    List<Recomendacao> findByIdUsuarioAndRegra(Long idUsuario, String regra);

    List<Recomendacao> findByIdUsuarioAndStatusOrderByDataCriacaoDesc(
            Long idUsuario, StatusRecomendacao status);
}
