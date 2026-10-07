package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.GastoFixoRequest;
import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.application.service.GastoFixoValidator;
import com.br.startup.tolevBack.finance.internal.entity.TransacaoRecorrente;
import com.br.startup.tolevBack.finance.internal.mapper.GastoFixoMapper;
import com.br.startup.tolevBack.finance.internal.repository.ITransacaoRecorrenteRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UpdateGastoFixoService {

    private final ITransacaoRecorrenteRepository repository;

    @Transactional
    public GastoFixoResponse execute(Long id, Long idUsuario, GastoFixoRequest request) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário do gasto fixo.");
        }

        TransacaoRecorrente gasto = repository.findByIdAndIdUsuario(id, idUsuario)
                .orElseThrow(() -> new NotFoundException("Gasto fixo não encontrado com id: " + id));

        gasto.setDescricao(GastoFixoValidator.nomeValido(request.nome()));
        gasto.setValor(GastoFixoValidator.valorValido(request.valor()));
        gasto.setDiaRecorrencia(GastoFixoValidator.diaValido(request.diaVencimento()));
        // Editar é a forma mais forte de confirmar: a pessoa olhou o valor e
        // disse qual é o certo.
        gasto.setConfirmadoEm(LocalDate.now());

        return GastoFixoMapper.toResponse(repository.save(gasto));
    }
}
