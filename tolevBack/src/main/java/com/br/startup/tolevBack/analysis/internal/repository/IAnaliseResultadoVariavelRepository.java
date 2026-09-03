package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultado;
import com.br.startup.tolevBack.analysis.internal.entity.AnaliseResultadoVariavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAnaliseResultadoVariavelRepository extends JpaRepository<AnaliseResultadoVariavel, Long> {
    List<AnaliseResultadoVariavel> findByAnaliseResultado(AnaliseResultado resultado);
    void deleteByAnaliseResultado(AnaliseResultado resultado);
}
