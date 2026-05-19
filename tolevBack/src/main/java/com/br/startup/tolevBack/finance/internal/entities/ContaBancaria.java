package com.br.startup.tolevBack.finance.internal.entities;

import com.br.startup.tolevBack.finance.enums.Moeda;
import com.br.startup.tolevBack.finance.enums.StatusConta;
import com.br.startup.tolevBack.finance.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_conta_bancaria")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banco")
    private Banco banco;

    private String numeroConta;

    @Enumerated(EnumType.STRING)
    private TipoConta tipoConta;

    private Boolean contaConjunta;
    private String nomeConta;

    @Enumerated(EnumType.STRING)
    private Moeda moeda;

    private BigDecimal saldoAtual;
    private BigDecimal saldoDisponivel;
    private BigDecimal limiteCredito;
    private LocalDate dataAbertura;

    @Enumerated(EnumType.STRING)
    private StatusConta statusConta;

    private LocalDateTime ultimaAtualizacao;
    private BigDecimal agencia;
    private BigDecimal mediaReceita;
    private BigDecimal mediaDespesa;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
