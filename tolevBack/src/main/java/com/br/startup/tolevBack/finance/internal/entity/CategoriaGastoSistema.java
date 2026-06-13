package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_categorias_gastos_sistema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaGastoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cor;

    @Enumerated(EnumType.STRING)
    private TipoCategoriaGasto tipo;

    private Boolean ativo;

    @OneToMany(mappedBy = "categoriaGastoSistema", fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    @OneToMany(mappedBy = "categoriaGastoSistema", fetch = FetchType.LAZY)
    private List<TransacaoRecorrente> transacoesRecorrentes;
}
