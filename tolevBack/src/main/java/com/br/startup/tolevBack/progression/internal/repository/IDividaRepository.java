package com.br.startup.tolevBack.progression.internal.repository;

import com.br.startup.tolevBack.progression.internal.entity.Divida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDividaRepository extends JpaRepository<Divida, Long> {
    List<Divida> findByIdUsuario(Long idUsuario);
}
