package com.br.startup.tolevBack.progression.application.dto.request;

import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DividaRequest(
    Long idUsuario,
    String nome,
    String banco,
    TipoDivida tipo,
    BigDecimal saldo,
    /** Juros mensal contratado, em % a.m. */
    BigDecimal juros,
    /** Multa por atraso, em %. */
    BigDecimal multaAtraso,
    /** Juros de mora, em % a.m. */
    BigDecimal jurosMora,
    Integer pesoEmocional,
    Integer quantidadeParcelas,
    LocalDate dataLiberacao,
    LocalDate dataPrimeiroVencimento,
    SistemaAmortizacao sistemaAmortizacao,
    RegimeJuros regimeJuros
) {}
