package com.br.startup.tolevBack.finance.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_bancos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String codigoBanco;
    private String logoUrl;
    private BigDecimal agencia;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "banco", fetch = FetchType.LAZY)
    private List<ContaBancaria> contasBancarias;
}
