package com.br.startup.tolevBack.analysis.internal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_recomendacao_entidade")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recomendacao")
    private Recomendacao recomendacao;

    private String tipoEntidade;
    private Long idEntidade;
    private String papelEntidade;
}
