package com.br.startup.tolevBack.finance.internal.entities;

import com.br.startup.tolevBack.finance.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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

    private String descricaoNormalizada;
    private Boolean parcelado;
}
