package com.br.startup.tolevBack.progression.internal.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_mapa_progressao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapaProgressao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlModelo;
    private String nomeMapa;
}
