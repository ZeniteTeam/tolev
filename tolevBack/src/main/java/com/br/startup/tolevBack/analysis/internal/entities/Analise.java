package com.br.startup.tolevBack.analysis.internal.entities;

import com.br.startup.tolevBack.analysis.enums.StatusAnalise;
import com.br.startup.tolevBack.analysis.enums.TipoAnalise;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_analises")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    private TipoAnalise tipo;

    private String origem;
    private String resultadoResumo;
    private String relevancia;
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private StatusAnalise status;

    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private Boolean acionavel;
}
