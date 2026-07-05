package com.br.startup.tolevBack.progression.application.usecase.commands.Debts;

import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.internal.entity.Divida;
import com.br.startup.tolevBack.progression.internal.mapper.DividaMapper;
import com.br.startup.tolevBack.progression.internal.repository.IDividaRepository;
import com.br.startup.tolevBack.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class UpdateDividaService {

    private final IDividaRepository dividaRepository;

    @Transactional
    public DividaResponse execute(Long id, DividaRequest request) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dívida não encontrada com id: " + id));
        int quantidade = request.quantidadeParcelas() != null ? request.quantidadeParcelas() : 0;
        BigDecimal saldo = request.saldo() != null ? request.saldo() : BigDecimal.ZERO;
        BigDecimal parcelaMinima = quantidade > 0
                ? saldo.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        divida.setNomeDivida(request.nome());
        divida.setBanco(request.banco());
        divida.setTipo(request.tipo());
        divida.setValorDivida(saldo);
        divida.setTaxaJuros(request.juros());
        divida.setParcelaMinima(parcelaMinima);
        divida.setPesoEmocional(request.pesoEmocional());
        divida.setQuantidadeParcelas(request.quantidadeParcelas());
        Divida saved = dividaRepository.save(divida);
        return DividaMapper.toResponse(saved);
    }
}
