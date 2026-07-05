package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;

import java.math.BigDecimal;
import java.util.List;

public record DividaResponse(
    Long id,
    Long idUsuario,
    String nome,
    String banco,
    TipoDivida tipo,
    BigDecimal saldo,
    BigDecimal juros,
    BigDecimal parcelaMinima,
    Integer pesoEmocional,
    Integer quantidadeParcelas,
    List<ParcelaResponse> parcelas
) {}
