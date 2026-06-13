package com.br.startup.tolevBack.progression.internal.entity;

import com.br.startup.tolevBack.progression.internal.enums.CategoriaMeta;
import com.br.startup.tolevBack.progression.internal.enums.StatusMeta;
import com.br.startup.tolevBack.progression.internal.enums.TipoMeta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    @Enumerated(EnumType.STRING)
    private CategoriaMeta categoria;

    private LocalDate dataLimite;
    private String recompensa;
    private String motivacaoMeta;

    @OneToOne(mappedBy = "meta", fetch = FetchType.LAZY)
    private ProgressoMeta progressoMeta;
}
