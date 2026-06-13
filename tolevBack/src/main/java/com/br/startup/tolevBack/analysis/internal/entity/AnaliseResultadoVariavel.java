package com.br.startup.tolevBack.analysis.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_analise_resultado_variavel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseResultadoVariavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resultado")
    private AnaliseResultado analiseResultado;

    private String nomeVariavel;
    private String valorVariavel;
    private BigDecimal valorFaixa;
    private BigDecimal peso;
    private BigDecimal coeficiente;
    private String impactoResultado;
    private String faixaReferencia;
    private LocalDate dataRegistro;
}
