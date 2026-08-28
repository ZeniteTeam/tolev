package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseEntidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAnaliseEntidadeRepository extends JpaRepository<AnaliseEntidade, Long> {
    List<AnaliseEntidade> findByAnalise(Analise analise);
    void deleteByAnalise(Analise analise);
}
