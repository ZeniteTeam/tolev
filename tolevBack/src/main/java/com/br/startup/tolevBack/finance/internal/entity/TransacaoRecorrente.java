package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma despesa que se repete todo mês.
 *
 * <p>Cobre os dois lados da mesma coisa: o gasto fixo que o usuário declara
 * (sem conta bancária, com {@code idUsuario} e {@code diaRecorrencia}) e a
 * recorrência detectada num extrato importado (com conta e vendedor). Duas
 * tabelas para isso divergiriam no primeiro mês.
 */
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

    private Long idUsuario;

    /** Nulo no gasto fixo digitado à mão: aluguel em dinheiro não sai de conta. */
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

    /**
     * Quando a pessoa confirmou pela última vez que este valor continua
     * valendo. Nulo = nunca confirmado. É o que separa "R$ 1.200 de aluguel,
     * conferido este mês" de "R$ 1.200 que alguém digitou em março".
     */
    private LocalDate confirmadoEm;
}
