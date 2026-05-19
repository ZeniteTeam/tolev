package com.br.startup.tolevBack.progression.internal.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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
}
