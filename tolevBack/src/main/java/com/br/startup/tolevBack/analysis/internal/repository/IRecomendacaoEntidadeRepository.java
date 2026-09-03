package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.RecomendacaoEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRecomendacaoEntidadeRepository extends JpaRepository<RecomendacaoEntidade, Long> {
}
