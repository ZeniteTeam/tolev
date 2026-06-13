package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVendorRepository extends JpaRepository<Vendedor, Long> {
}
