package com.br.startup.tolevBack.analysis.internal.entities;

import com.br.startup.tolevBack.analysis.enums.NivelRisco;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_analise_resultado")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise")
    private Analise analise;

    private String classificacao;
    private BigDecimal score;
    private BigDecimal probabilidade;
    private BigDecimal coeficienteGeral;

    @Enumerated(EnumType.STRING)
    private NivelRisco nivelRisco;

    private String modeloUtilizado;
    private String versaoModelo;
    private String explicacao;
    private LocalDateTime dataCriacao;
}
