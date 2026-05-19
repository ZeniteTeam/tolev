package com.br.startup.tolevBack.finance.internal.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private BigDecimal agencia;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
