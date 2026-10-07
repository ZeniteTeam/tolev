package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.internal.mapper.GastoFixoMapper;
import com.br.startup.tolevBack.finance.internal.repository.ITransacaoRecorrenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Os gastos fixos vivos do usuário, na ordem em que caem no mês. */
@Service
@RequiredArgsConstructor
public class GetGastosFixosService {

    private final ITransacaoRecorrenteRepository repository;

    @Transactional(readOnly = true)
    public List<GastoFixoResponse> execute(Long idUsuario) {
        return repository.findByIdUsuarioAndAtivoTrueOrderByDiaRecorrenciaAscIdAsc(idUsuario)
                .stream()
                .map(GastoFixoMapper::toResponse)
                .toList();
    }
}
