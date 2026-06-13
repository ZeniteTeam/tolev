package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByIdUsuario(Long idUsuario);
}
