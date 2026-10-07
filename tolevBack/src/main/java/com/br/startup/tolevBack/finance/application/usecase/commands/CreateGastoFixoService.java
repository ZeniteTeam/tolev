package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.application.dto.request.GastoFixoRequest;
import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.application.service.GastoFixoValidator;
import com.br.startup.tolevBack.finance.internal.entity.TransacaoRecorrente;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import com.br.startup.tolevBack.finance.internal.mapper.GastoFixoMapper;
import com.br.startup.tolevBack.finance.internal.repository.ITransacaoRecorrenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateGastoFixoService {

    private final ITransacaoRecorrenteRepository repository;

    @Transactional
    public GastoFixoResponse execute(GastoFixoRequest request) {
        if (request.idUsuario() == null) {
            throw new IllegalArgumentException("Informe o usuário do gasto fixo.");
        }

        TransacaoRecorrente gasto = repository.save(
                TransacaoRecorrente.builder()
                        .idUsuario(request.idUsuario())
                        .descricao(GastoFixoValidator.nomeValido(request.nome()))
                        .valor(GastoFixoValidator.valorValido(request.valor()))
                        .diaRecorrencia(GastoFixoValidator.diaValido(request.diaVencimento()))
                        .tipo(TipoTransacao.DESPESA)
                        .parcelado(false)
                        .ativo(true)
                        .dataInicio(LocalDate.now())
                        // Nasce confirmado: a pessoa acabou de digitar o valor, e
                        // pedir que ela confirme o que digitou há um segundo ensina
                        // a confirmar sem olhar.
                        .confirmadoEm(LocalDate.now())
                        .build());

        return GastoFixoMapper.toResponse(gasto);
    }
}
