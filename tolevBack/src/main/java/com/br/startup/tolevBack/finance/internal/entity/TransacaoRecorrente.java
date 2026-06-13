package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_transacoes_recorrentes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoRecorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_bancaria")
    private ContaBancaria contaBancaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private String descricao;
    private String descricaoNormalizada;
    private Boolean parcelado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_gasto_sistema")
    private CategoriaGastoSistema categoriaGastoSistema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_gasto_usuario")
    private CategoriaGastoUsuario categoriaGastoUsuario;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    private Integer diaRecorrencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativo;
}
