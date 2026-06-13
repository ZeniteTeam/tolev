package com.br.startup.tolevBack.users.application.usecase.queries;

import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.application.dto.response.UsuarioAssinaturaResponse;
import com.br.startup.tolevBack.users.internal.entity.Usuario;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserAssinaturasService {

    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UsuarioAssinaturaResponse> execute(Long idUsuario) {
        Usuario usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + idUsuario));
        return usuario.getUsuarioAssinaturas()
                .stream()
                .map(UserMapper::toAssinaturaResponse)
                .toList();
    }
}
