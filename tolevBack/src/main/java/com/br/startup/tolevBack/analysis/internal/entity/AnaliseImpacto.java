package com.br.startup.tolevBack.analysis.internal.entity;

import com.br.startup.tolevBack.analysis.internal.enums.TipoImpacto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_analise_impacto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseImpacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise")
    private Analise analise;

    @Enumerated(EnumType.STRING)
    private TipoImpacto tipoImpacto;

    /**
     * Nome da {@link com.br.startup.tolevBack.analysis.internal.enums.RegraAnalise}
     * que gerou o achado. É a identidade dele entre análises de dias diferentes —
     * sem ela não dá para contar recorrência.
     */
    private String regra;

    private String entidadeOrigemTipo;
    private Long entidadeOrigemId;
    private String entidadeImpactadaTipo;
    private Long entidadeImpactadaId;
    private String descricao;
    private String gravidade;
    private BigDecimal scoreImpacto;
    private BigDecimal impactoEstimadoValor;
    private BigDecimal impactoTemporalAnual;
    private BigDecimal impactoTemporalMensal;
}
