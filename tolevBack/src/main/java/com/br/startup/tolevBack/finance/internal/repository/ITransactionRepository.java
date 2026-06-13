package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaBancaria_IdUsuario(Long idUsuario);
}
