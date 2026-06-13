package com.br.startup.tolevBack.analysis.internal.repository;

import com.br.startup.tolevBack.analysis.internal.entity.Analise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAnaliseRepository extends JpaRepository<Analise, Long> {
    List<Analise> findByIdUsuario(Long idUsuario);
    List<Analise> findByIdUsuarioAndTipo(Long idUsuario, TipoAnalise tipo);
    Optional<Analise> findTopByIdUsuarioOrderByDataCriacaoDesc(Long idUsuario);
}
