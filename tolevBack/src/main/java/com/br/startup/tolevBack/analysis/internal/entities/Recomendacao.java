package com.br.startup.tolevBack.analysis.internal.entities;

import com.br.startup.tolevBack.analysis.enums.Prioridade;
import com.br.startup.tolevBack.analysis.enums.StatusRecomendacao;
import com.br.startup.tolevBack.analysis.enums.TipoRecomendacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private String titulo;
    private String descricao;
    private BigDecimal dificuldade;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    private StatusRecomendacao status;

    private LocalDateTime dataCriacao;
}
