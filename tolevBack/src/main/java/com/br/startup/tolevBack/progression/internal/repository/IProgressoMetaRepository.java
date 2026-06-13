package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.Meta;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProgressoMetaRepository extends JpaRepository<ProgressoMeta, Long> {
    Optional<ProgressoMeta> findByMeta(Meta meta);
}
