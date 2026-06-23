package com.br.startup.tolevBack.progression.application.usecase.queries.Debts;

import com.br.startup.tolevBack.progression.application.dto.response.DebtRiskResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetDebtRiskService {

    private final IDividaRepository dividaRepository;

    @Transactional(readOnly = true)
    public DebtRiskResponse execute(Long idUsuario) {
        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);

        int total = dividas.size();
        int ativas = (int) dividas.stream().filter(d -> StatusDivida.ATIVA.equals(d.getStatus())).count();
        int atrasadas = (int) dividas.stream().filter(d -> StatusDivida.ATRASADA.equals(d.getStatus())).count();
        int pagas = (int) dividas.stream().filter(d -> StatusDivida.PAGA.equals(d.getStatus())).count();

        BigDecimal valorTotal = dividas.stream()
                .map(Divida::getValorDivida)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String nivelRisco = calcularNivelRisco(total, atrasadas);

        return new DebtRiskResponse(idUsuario, nivelRisco, total, ativas, atrasadas, pagas, valorTotal);
    }

    private String calcularNivelRisco(int total, int atrasadas) {
        if (total == 0) return "BAIXO";
        double percentualAtrasadas = (double) atrasadas / total;
        if (percentualAtrasadas >= 0.5) return "ALTO";
        if (percentualAtrasadas >= 0.25) return "MEDIO";
        return "BAIXO";
    }
}
