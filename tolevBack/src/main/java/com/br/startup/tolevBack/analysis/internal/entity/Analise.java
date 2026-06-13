package com.br.startup.tolevBack.analysis.internal.entity;

import com.br.startup.tolevBack.analysis.internal.enums.StatusAnalise;
import com.br.startup.tolevBack.analysis.internal.enums.TipoAnalise;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_analises")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    private TipoAnalise tipo;

    private String origem;
    private String resultadoResumo;
    private String relevancia;
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private StatusAnalise status;

    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private Boolean acionavel;

    @OneToMany(mappedBy = "analise", fetch = FetchType.LAZY)
    private List<AnaliseEntidade> entidades;

    @OneToMany(mappedBy = "analise", fetch = FetchType.LAZY)
    private List<AnaliseImpacto> impactos;

    @OneToOne(mappedBy = "analise", fetch = FetchType.LAZY)
    private AnaliseResultado resultado;

    @OneToMany(mappedBy = "analise", fetch = FetchType.LAZY)
    private List<Recomendacao> recomendacoes;
}
