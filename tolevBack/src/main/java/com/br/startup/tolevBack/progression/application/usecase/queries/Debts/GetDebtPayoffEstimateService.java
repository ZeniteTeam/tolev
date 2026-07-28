package com.br.startup.tolevBack.progression.application.usecase.queries.Debts;

import com.br.startup.tolevBack.progression.application.dto.response.DebtPayoffEstimateResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetDebtPayoffEstimateService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;

    @Transactional(readOnly = true)
    public DebtPayoffEstimateResponse execute(Long idUsuario) {
        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);

        BigDecimal totalDividas = dividas.stream()
                .map(Divida::getValorDivida)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPago = dividas.stream()
                .map(divida -> {
                    ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida).orElse(null);
                    if (progresso == null || progresso.getProgresso() == null || divida.getValorDivida() == null) {
                        return BigDecimal.ZERO;
                    }
                    return divida.getValorDivida()
                            .multiply(progresso.getProgresso())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRestante = totalDividas.subtract(totalPago);
        BigDecimal percentualPago = totalDividas.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalPago.divide(totalDividas, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        return new DebtPayoffEstimateResponse(idUsuario, totalDividas, totalPago, totalRestante, percentualPago);
    }
}
