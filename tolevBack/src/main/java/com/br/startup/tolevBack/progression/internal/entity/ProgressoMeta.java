package com.br.startup.tolevBack.progression.internal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_progresso_meta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressoMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_meta")
    private Meta meta;

    @Min(0)
    private BigDecimal progresso;
    private BigDecimal peso;

    private Double percentualQuitado;

    private LocalDate dataReferencia;
}
