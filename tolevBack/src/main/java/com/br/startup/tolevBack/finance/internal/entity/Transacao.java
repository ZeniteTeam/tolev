package com.br.startup.tolevBack.finance.internal.entity;

import com.br.startup.tolevBack.finance.internal.enums.MetodoPagamento;
import com.br.startup.tolevBack.finance.internal.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_transacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Dono da transação. Fica direto aqui (e não só via conta bancária) porque
     * um lançamento manual em dinheiro não tem conta nenhuma associada.
     */
    private Long idUsuario;

    /** Opcional: só quando o gasto saiu de uma conta conectada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_bancaria")
    private ContaBancaria contaBancaria;

    /**
     * De qual banco veio o lançamento. Preenchido na importação de extrato, onde
     * o usuário escolhe o banco antes de subir o PDF; {@code null} no lançamento
     * digitado à mão, que pode ter saído do dinheiro da carteira.
     *
     * <p>Separado de {@link #contaBancaria} de propósito: aquilo é uma conta com
     * saldo, que a importação não cria nem movimenta. Isto é só a procedência —
     * e é o que faz o filtro por banco da tela de Finanças funcionar.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banco")
    private Banco banco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    private BigDecimal valor;
    private LocalDate dataTransacao;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private String descricao;
    private String descricaoNormalizada;
    private Boolean parcelado;
    private BigDecimal totalParcelas;
    private BigDecimal numeroParcela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_gasto_sistema")
    private CategoriaGastoSistema categoriaGastoSistema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_gasto_usuario")
    private CategoriaGastoUsuario categoriaGastoUsuario;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    /**
     * De qual importação de extrato esta transação veio. {@code null} é o caso
     * normal: lançamento digitado pelo usuário.
     *
     * <p>Id solto em vez de {@code @ManyToOne} porque o único uso é apagar o lote
     * inteiro ao desfazer uma importação — carregar a entidade não acrescenta nada.
     */
    private Long idImportacaoExtrato;
}
