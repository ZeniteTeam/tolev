package com.br.startup.tolevBack.users.application.usecase.commands;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse execute(UsuarioRequest request) {
        Usuario usuario = UserMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        return UserMapper.toResponse(userRepository.save(usuario));
    }
}
