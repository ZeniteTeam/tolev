package com.br.startup.tolevBack.users.application.usecase.commands;

import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.application.dto.request.UsuarioRequest;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UpdateUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse execute(Long id, UsuarioRequest request) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));
        usuario.setNome(request.nome());
        usuario.setGenero(request.genero());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setNomeUsuario(request.nomeUsuario());
        if (StringUtils.hasText(request.senha())) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        usuario.setEmail(request.email());
        return UserMapper.toResponse(userRepository.save(usuario));
    }
}
