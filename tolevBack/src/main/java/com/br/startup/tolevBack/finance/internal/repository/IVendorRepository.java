package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVendorRepository extends JpaRepository<Vendedor, Long> {

    /** Busca global: a Amazon é a mesma para todos os usuários. */
    Optional<Vendedor> findByNomeNormalizado(String nomeNormalizado);
}
