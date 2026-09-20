package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.internal.entity.TransacaoRecorrente;
import com.br.startup.tolevBack.finance.internal.repository.ITransacaoRecorrenteRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Tira o gasto da lista sem apagar a linha.
 *
 * <p>É desativação, não remoção: o histórico de quanto a pessoa gastava por mês
 * é o que vai permitir dizer, mais tarde, que a internet subiu R$ 30. Apagar a
 * linha apagaria junto a única prova de que ela existia.
 */
@Service
@RequiredArgsConstructor
public class DeleteGastoFixoService {

    private final ITransacaoRecorrenteRepository repository;

    @Transactional
    public void execute(Long id, Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário do gasto fixo.");
        }

        TransacaoRecorrente gasto = repository.findByIdAndIdUsuario(id, idUsuario)
                .orElseThrow(() -> new NotFoundException("Gasto fixo não encontrado com id: " + id));

        gasto.setAtivo(false);
        gasto.setDataFim(LocalDate.now());
        repository.save(gasto);
    }
}
