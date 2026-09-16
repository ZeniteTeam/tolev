package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoriaGastoSistemaRepository extends JpaRepository<CategoriaGastoSistema, Long> {

    List<CategoriaGastoSistema> findByAtivoTrueOrderByNomeAsc();

    /**
     * Busca pelo nome semeado em {@code V4__seed_categorias_gasto_sistema.sql}.
     * Usado para achar a categoria guarda-chuva ("Outros") sem cravar o id no
     * código — o id depende da ordem do INSERT, o nome não.
     */
    Optional<CategoriaGastoSistema> findByNomeIgnoreCaseAndTipoAndAtivoTrue(
            String nome, TipoCategoriaGasto tipo);
}
