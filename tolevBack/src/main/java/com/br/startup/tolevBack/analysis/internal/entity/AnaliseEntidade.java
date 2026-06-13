package com.br.startup.tolevBack.analysis.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_analise_entidade")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise")
    private Analise analise;

    private String tipoEntidade;
    private Long idEntidade;
    private String papelEntidade;
    private BigDecimal pesoEntidade;
}
