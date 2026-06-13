package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.TipoSimulacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_simulacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Simulacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    private String nome;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoSimulacao tipo;

    private String parametrosEntrada;
    private Boolean ativo;
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "simulacao", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulacaoResultado> resultados;
}
