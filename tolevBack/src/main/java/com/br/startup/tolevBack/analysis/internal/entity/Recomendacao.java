package com.br.startup.tolevBack.analysis.internal.entity;

import com.br.startup.tolevBack.analysis.internal.enums.Prioridade;
import com.br.startup.tolevBack.analysis.internal.enums.StatusRecomendacao;
import com.br.startup.tolevBack.analysis.internal.enums.TipoRecomendacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_recomendacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise")
    private Analise analise;

    @Enumerated(EnumType.STRING)
    private TipoRecomendacao tipo;

    /**
     * Regra que originou a recomendação. Usada para não recriar a mesma
     * recomendação toda vez que o achado é redetectado.
     */
    private String regra;

    private String titulo;
    private String descricao;
    private BigDecimal dificuldade;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    private StatusRecomendacao status;

    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "recomendacao", fetch = FetchType.LAZY)
    private List<RecomendacaoEntidade> entidades;
}
