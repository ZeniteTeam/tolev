package com.br.startup.tolevBack.users.application.usecase.queries;

import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserByIdService {

    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public UsuarioResponse execute(Long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));
        return UserMapper.toResponse(usuario);
    }
}
