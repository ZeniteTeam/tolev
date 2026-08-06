package com.br.startup.tolevBack.users.application.usecase.queries;

import com.br.startup.tolevBack.users.application.dto.request.UsuarioResendDTO;
import com.br.startup.tolevBack.users.internal.mapper.UserMapper;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserByReceberEmail {

    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResendDTO> execute(Boolean ativo) {
        var usuario = userRepository.findByReceberEmail(ativo);
        return usuario.stream().map(UserMapper::toResendResponse).toList();
    }

}
