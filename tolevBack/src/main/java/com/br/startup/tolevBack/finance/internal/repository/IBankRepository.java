package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBankRepository extends JpaRepository<Banco, Long> {
}
