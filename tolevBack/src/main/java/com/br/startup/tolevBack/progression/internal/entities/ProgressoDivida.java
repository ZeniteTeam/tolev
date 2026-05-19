package com.br.startup.tolevBack.progression.internal.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_progresso_divida")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressoDivida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_divida")
    private Divida divida;

    private BigDecimal progresso;
    private BigDecimal peso;
    private LocalDate ultimoProgresso;
}
