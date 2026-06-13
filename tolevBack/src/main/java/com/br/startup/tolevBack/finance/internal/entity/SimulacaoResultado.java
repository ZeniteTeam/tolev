package com.br.startup.tolevBack.finance.internal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_simulacao_resultado")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoResultado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacao_id")
    private Simulacao simulacao;

    private LocalDate dataReferencia;
    private BigDecimal valorProjetado;
    private BigDecimal saldoProjetado;
    private BigDecimal economiaGerada;
    private String observacoes;
    private LocalDateTime criadoEm;
}
