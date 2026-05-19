package com.br.startup.tolevBack.support.internal.entities;

import com.br.startup.tolevBack.support.enums.TipoFeedback;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_feedbacks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoFeedback tipo;
}
