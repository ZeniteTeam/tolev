package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<ContaBancaria, Long> {
    List<ContaBancaria> findByIdUsuario(Long idUsuario);
}
