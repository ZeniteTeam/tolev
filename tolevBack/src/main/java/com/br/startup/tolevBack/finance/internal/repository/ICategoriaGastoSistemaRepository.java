package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoriaGastoSistemaRepository extends JpaRepository<CategoriaGastoSistema, Long> {

    List<CategoriaGastoSistema> findByAtivoTrueOrderByNomeAsc();
}
