package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.TipoCategoriaGasto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_categorias_gastos_usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaGastoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    private String nome;
    private String cor;

    @Enumerated(EnumType.STRING)
    private TipoCategoriaGasto tipo;

    private Boolean ativo;

    @OneToMany(mappedBy = "categoriaGastoUsuario", fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    @OneToMany(mappedBy = "categoriaGastoUsuario", fetch = FetchType.LAZY)
    private List<TransacaoRecorrente> transacoesRecorrentes;
}
