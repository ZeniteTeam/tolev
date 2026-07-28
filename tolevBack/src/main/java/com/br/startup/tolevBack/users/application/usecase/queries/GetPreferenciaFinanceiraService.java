package com.br.startup.tolevBack.users.application.usecase.queries;

import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.internal.mapper.PreferenciaFinanceiraMapper;
import com.br.startup.tolevBack.users.internal.repository.IPreferenciaFinanceiraRepository;
import com.br.startup.tolevBack.users.internal.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPreferenciaFinanceiraService {

    private final IPreferenciaFinanceiraRepository preferenciaRepository;
    private final IUserRepository userRepository;

    /**
     * Retorna as preferências do usuário. Se ele ainda não personalizou nada,
     * devolve os valores padrão (sem persistir).
     */
    @Transactional(readOnly = true)
    public PreferenciaFinanceiraResponse execute(Long idUsuario) {
        if (!userRepository.existsById(idUsuario)) {
            throw new NotFoundException("Usuário não encontrado com id: " + idUsuario);
        }
        return preferenciaRepository.findByIdUsuario(idUsuario)
                .map(PreferenciaFinanceiraMapper::toResponse)
                .orElseGet(() -> PreferenciaFinanceiraMapper.toResponse(
                        PreferenciaFinanceiraMapper.defaults(idUsuario)));
    }
}
