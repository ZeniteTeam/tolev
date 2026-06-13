package com.br.startup.tolevBack.users.internal.repository;

import com.br.startup.tolevBack.users.internal.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);
}
