package com.br.startup.tolevBack.progression.internal.entities;

import com.br.startup.tolevBack.finance.enums.StatusDivida;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_dividas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Divida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;
    private BigDecimal valorDivida;

    @Enumerated(EnumType.STRING)
    private StatusDivida status;
}
