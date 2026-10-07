package com.br.startup.tolevBack.finance.application.usecase.commands;

import com.br.startup.tolevBack.finance.internal.repository.ITransacaoRecorrenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * "Continua tudo assim": carimba a data de hoje em todos os gastos vivos.
 *
 * <p>É o gesto que mantém o cadastro honesto sem virar tarefa. Quem precisa
 * mudar um valor edita aquele gasto — e a edição confirma sozinha. Quem não
 * precisa mudar nada resolve o mês inteiro num toque, que é o caso comum:
 * gasto fixo muda pouco, e é justamente por isso que ele envelhece sem ninguém
 * notar.
 */
@Service
@RequiredArgsConstructor
public class ConfirmarGastosFixosService {

    private final ITransacaoRecorrenteRepository repository;

    @Transactional
    public int execute(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o usuário dos gastos fixos.");
        }
        return repository.confirmarTodos(idUsuario, LocalDate.now());
    }
}
