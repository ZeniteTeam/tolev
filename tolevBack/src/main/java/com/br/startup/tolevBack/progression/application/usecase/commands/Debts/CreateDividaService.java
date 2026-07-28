package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.entity.ParcelaDivida;
import com.br.startup.tolevBack.progression.internal.entity.ProgressoDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusDivida;
import com.br.startup.tolevBack.progression.internal.enums.StatusParcela;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IParcelaDividaRepository;
import com.br.startup.tolevBack.progression.internal.repository.IProgressoDividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateDividaService {

    private final IDividaRepository dividaRepository;
    private final IProgressoDividaRepository progressoDividaRepository;
    private final IParcelaDividaRepository parcelaDividaRepository;

    @Transactional
    public DividaResponse execute(DividaRequest request) {
        int quantidade = request.quantidadeParcelas() != null ? request.quantidadeParcelas() : 0;
        BigDecimal saldo = request.saldo() != null ? request.saldo() : BigDecimal.ZERO;

        // A parcela mínima é derivada: saldo ÷ quantidade de parcelas. Assim
        // quantidade × parcela mínima bate exatamente com o saldo da dívida.
        BigDecimal parcelaMinima = quantidade > 0
                ? saldo.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Divida divida = Divida.builder()
                .idUsuario(request.idUsuario())
                .nomeDivida(request.nome())
                .banco(request.banco())
                .tipo(request.tipo())
                .valorDivida(saldo)
                .taxaJuros(request.juros())
                .parcelaMinima(parcelaMinima)
                .pesoEmocional(request.pesoEmocional())
                .quantidadeParcelas(request.quantidadeParcelas())
                .status(StatusDivida.ATIVA)
                .build();

        divida = dividaRepository.save(divida);

        ProgressoDivida progresso = new ProgressoDivida();
        progresso.setDivida(divida);
        progresso.setProgresso(BigDecimal.ZERO);
        progressoDividaRepository.save(progresso);

        divida.setParcelas(gerarParcelas(divida, saldo, quantidade, parcelaMinima));

        return DividaMapper.toResponse(divida);
    }

    /**
     * Generates the debt's installments (one row per parcela), all PENDENTE. Every
     * parcela is worth {@code parcelaMinima}, except the last, which absorbs the
     * rounding remainder so the sum matches the saldo to the cent.
     */
    private List<ParcelaDivida> gerarParcelas(Divida divida, BigDecimal saldo, int quantidade, BigDecimal parcelaMinima) {
        if (quantidade <= 0) {
            return new ArrayList<>();
        }

        LocalDate base = LocalDate.now();
        BigDecimal acumulado = BigDecimal.ZERO;

        List<ParcelaDivida> parcelas = new ArrayList<>();
        for (int numero = 1; numero <= quantidade; numero++) {
            BigDecimal valor = numero < quantidade
                    ? parcelaMinima
                    : saldo.subtract(acumulado); // última parcela fecha o saldo
            acumulado = acumulado.add(valor);

            parcelas.add(ParcelaDivida.builder()
                    .divida(divida)
                    .numeroParcela(numero)
                    .valorTotal(valor)
                    .valorPrincipal(valor)
                    .status(StatusParcela.PENDENTE)
                    .dataVencimento(base.plusMonths(numero))
                    .build());
        }
        return parcelaDividaRepository.saveAll(parcelas);
    }
}
