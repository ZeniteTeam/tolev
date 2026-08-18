package com.br.startup.tolevBack.finance.internal.repository;

import com.br.startup.tolevBack.finance.internal.entity.CategoriaGastoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoriaGastoUsuarioRepository extends JpaRepository<CategoriaGastoUsuario, Long> {

    List<CategoriaGastoUsuario> findByIdUsuarioAndAtivoTrueOrderByNomeAsc(Long idUsuario);
}
