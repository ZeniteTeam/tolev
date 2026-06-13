package com.br.startup.tolevBack.finance.application.usecase.queries;

import com.br.startup.tolevBack.finance.application.dto.response.FinancialOverviewResponse;
import com.br.startup.tolevBack.finance.internal.entity.ContaBancaria;
import com.br.startup.tolevBack.finance.internal.repository.IAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetFinancialOverviewService {

    private final IAccountRepository accountRepository;

    @Transactional(readOnly = true)
    public FinancialOverviewResponse execute(Long idUsuario) {
        List<ContaBancaria> contas = accountRepository.findByIdUsuario(idUsuario);

        BigDecimal totalSaldo = sum(contas.stream().map(ContaBancaria::getSaldoAtual).toList());
        BigDecimal totalSaldoDisponivel = sum(contas.stream().map(ContaBancaria::getSaldoDisponivel).toList());
        BigDecimal totalLimiteCredito = sum(contas.stream().map(ContaBancaria::getLimiteCredito).toList());
        BigDecimal mediaReceita = sum(contas.stream().map(ContaBancaria::getMediaReceita).toList());
        BigDecimal mediaDespesa = sum(contas.stream().map(ContaBancaria::getMediaDespesa).toList());

        return new FinancialOverviewResponse(
                idUsuario,
                totalSaldo,
                totalSaldoDisponivel,
                totalLimiteCredito,
                mediaReceita,
                mediaDespesa,
                contas.size()
        );
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
