package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.MapaModulo;
import com.br.startup.tolevBack.progression.internal.entity.ModuloProgressaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IModuloProgressaoUsuarioRepository extends JpaRepository<ModuloProgressaoUsuario, Long> {
    List<ModuloProgressaoUsuario> findByIdUsuario(Long idUsuario);
    Optional<ModuloProgressaoUsuario> findByMapaModuloAndIdUsuario(MapaModulo mapaModulo, Long idUsuario);
}
