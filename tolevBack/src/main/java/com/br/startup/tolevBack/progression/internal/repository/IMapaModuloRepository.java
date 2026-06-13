package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.MapaModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMapaModuloRepository extends JpaRepository<MapaModulo, Long> {
    List<MapaModulo> findByMapaProgressao_Id(Long idMapaProgressao);
}
