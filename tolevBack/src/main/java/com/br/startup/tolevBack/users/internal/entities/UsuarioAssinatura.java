package com.br.startup.tolevBack.users.internal.entities;

import com.br.startup.tolevBack.users.internal.enums.StatusAssinatura;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_usuario_assinaturas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAssinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assinatura")
    private Assinatura assinatura;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusAssinatura status;
}
