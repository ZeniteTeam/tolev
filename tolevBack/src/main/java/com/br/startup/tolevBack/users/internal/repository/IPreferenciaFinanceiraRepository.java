package com.br.startup.tolevBack.users.internal.repository;

import com.br.startup.tolevBack.users.internal.entity.PreferenciaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPreferenciaFinanceiraRepository extends JpaRepository<PreferenciaFinanceira, Long> {
    Optional<PreferenciaFinanceira> findByIdUsuario(Long idUsuario);
}
