package com.br.startup.tolevBack.progression.application.usecase.queries.Debts;

import com.br.startup.tolevBack.progression.application.dto.response.DebtStrategyItemResponse;
import com.br.startup.tolevBack.progression.application.dto.response.DebtStrategyPlanResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import com.br.startup.tolevBack.users.application.dto.response.PreferenciaFinanceiraResponse;
import com.br.startup.tolevBack.users.integration.api.UserIntegrationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Monta o plano de quitação das dívidas do usuário ordenando-as segundo o
 * {@code metodoQuitacao} salvo nas preferências. É o ponto onde a escolha do
 * método passa a impactar concretamente as projeções e recomendações.
 */
@Service
@RequiredArgsConstructor
public class GetDebtStrategyPlanService {

    private static final BigDecimal CEM = new BigDecimal("100");

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;
    private final UserIntegrationApi userIntegration;

    @Transactional(readOnly = true)
    public DebtStrategyPlanResponse execute(Long idUsuario) {
        PreferenciaFinanceiraResponse pref = userIntegration.getPreferencias(idUsuario);
        String metodo = pref.metodoQuitacao().name();

        List<Divida> dividas = dividaRepository.findByIdUsuario(idUsuario);

        // Calcula o saldo restante de cada dívida a partir do progresso registrado.
        record DividaSaldo(Divida divida, BigDecimal saldoRestante) {}
        List<DividaSaldo> comSaldo = new ArrayList<>();
        for (Divida divida : dividas) {
            comSaldo.add(new DividaSaldo(divida, saldoRestante(divida)));
        }

        Comparator<DividaSaldo> ordenacao = switch (pref.metodoQuitacao()) {
            // Maiores juros primeiro.
            case AVALANCHE -> Comparator.comparing(
                    ds -> nvl(ds.divida().getTaxaJuros()), Comparator.reverseOrder());
            // Menores saldos primeiro.
            case SNOWBALL -> Comparator.comparing(DividaSaldo::saldoRestante);
            // Maior peso emocional primeiro.
            case TSUNAMI -> Comparator.comparing(
                    (DividaSaldo ds) -> ds.divida().getPesoEmocional() == null ? 0 : ds.divida().getPesoEmocional(),
                    Comparator.reverseOrder());
        };

        List<DividaSaldo> ordenadas = comSaldo.stream().sorted(ordenacao).toList();

        List<DebtStrategyItemResponse> itens = new ArrayList<>();
        int ordem = 1;
        for (DividaSaldo ds : ordenadas) {
            Divida d = ds.divida();
            itens.add(new DebtStrategyItemResponse(
                    d.getId(),
                    d.getNomeDivida(),
                    d.getBanco(),
                    ds.saldoRestante(),
                    d.getTaxaJuros(),
                    d.getParcelaMinima(),
                    d.getPesoEmocional(),
                    ordem,
                    ordem == 1
            ));
            ordem++;
        }

        BigDecimal totalRestante = ordenadas.stream()
                .map(DividaSaldo::saldoRestante)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DebtStrategyPlanResponse(
                idUsuario,
                metodo,
                criterio(metodo),
                nvl(pref.aporteExtraMensal()),
                totalRestante,
                itens
        );
    }

    private BigDecimal saldoRestante(Divida divida) {
        BigDecimal valor = nvl(divida.getValorDivida());
        ProgressoDivida progresso = progressoDividaRepository.findByDivida(divida).orElse(null);
        if (progresso == null || progresso.getProgresso() == null) {
            return valor;
        }
        BigDecimal pago = valor
                .multiply(progresso.getProgresso())
                .divide(CEM, 2, RoundingMode.HALF_UP);
        BigDecimal restante = valor.subtract(pago);
        return restante.max(BigDecimal.ZERO);
    }

    private String criterio(String metodo) {
        return switch (metodo) {
            case "AVALANCHE" -> "juros";
            case "SNOWBALL" -> "saldo";
            case "TSUNAMI" -> "emocional";
            default -> "juros";
        };
    }

    private static BigDecimal nvl(BigDecimal v) {
        return Objects.requireNonNullElse(v, BigDecimal.ZERO);
    }
}
