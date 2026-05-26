package com.br.startup.tolevBack.users.internal.repositories;

import com.br.startup.tolevBack.users.internal.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);
}
