package com.br.startup.tolevBack.progression.application.dto.response;

import com.br.startup.tolevBack.progression.internal.enums.RegimeJuros;
import com.br.startup.tolevBack.progression.internal.enums.SistemaAmortizacao;
import com.br.startup.tolevBack.progression.internal.enums.TipoDivida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DividaResponse(
    Long id,
    Long idUsuario,
    String nome,
    String banco,
    TipoDivida tipo,
    BigDecimal saldo,
    BigDecimal juros,
    BigDecimal multaAtraso,
    BigDecimal jurosMora,
    BigDecimal parcelaMinima,
    Integer pesoEmocional,
    Integer quantidadeParcelas,
    LocalDate dataLiberacao,
    LocalDate dataPrimeiroVencimento,
    SistemaAmortizacao sistemaAmortizacao,
    RegimeJuros regimeJuros,
    /** Soma de juros de todas as parcelas geradas. */
    BigDecimal totalJuros,
    /** Saldo + totalJuros: quanto a dívida custa até o fim. */
    BigDecimal totalAPagar,
    List<ParcelaResponse> parcelas
) {}
