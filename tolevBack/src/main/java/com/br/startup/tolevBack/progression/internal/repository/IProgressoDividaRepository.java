package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProgressoDividaRepository extends JpaRepository<ProgressoDivida, Long> {
    Optional<ProgressoDivida> findByDivida(Divida divida);
}
