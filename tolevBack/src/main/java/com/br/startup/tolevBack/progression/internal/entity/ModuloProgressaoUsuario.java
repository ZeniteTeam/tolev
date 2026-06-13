package com.br.startup.tolevBack.progression.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_modulo_progressao_usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloProgressaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mapa_modulo")
    private MapaModulo mapaModulo;

    private Long idUsuario;
    private BigDecimal progressao;
    private Boolean concluido;
    private LocalDate dataConclusao;
}
