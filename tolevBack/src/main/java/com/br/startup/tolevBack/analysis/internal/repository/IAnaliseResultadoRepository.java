package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAnaliseResultadoRepository extends JpaRepository<AnaliseResultado, Long> {
    Optional<AnaliseResultado> findByAnalise(Analise analise);
}
