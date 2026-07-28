package com.br.startup.tolevBack.progression.application.dto.request;

import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;

import java.math.BigDecimal;

public record DividaRequest(
    Long idUsuario,
    String nome,
    String banco,
    TipoDivida tipo,
    BigDecimal saldo,
    BigDecimal juros,
    BigDecimal parcelaMinima,
    Integer pesoEmocional,
    Integer quantidadeParcelas
) {}
