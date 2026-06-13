package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRecomendacaoRepository extends JpaRepository<Recomendacao, Long> {
    List<Recomendacao> findByIdUsuario(Long idUsuario);
}
