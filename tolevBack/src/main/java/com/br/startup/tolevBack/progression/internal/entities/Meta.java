package com.br.startup.tolevBack.progression.internal.entities;

import com.br.startup.tolevBack.progression.enums.StatusMeta;
import com.br.startup.tolevBack.progression.enums.TipoMeta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_metas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;
    private String nomeMeta;
    private BigDecimal valorMeta;

    @Enumerated(EnumType.STRING)
    private StatusMeta status;

    @Enumerated(EnumType.STRING)
    private TipoMeta tipo;
}
