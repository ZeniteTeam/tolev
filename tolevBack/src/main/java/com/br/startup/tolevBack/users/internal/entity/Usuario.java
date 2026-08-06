package com.br.startup.tolevBack.users.internal.entity;

import com.br.startup.tolevBack.users.internal.enums.ObjetivoPrincipal;
import com.br.startup.tolevBack.users.internal.enums.PapelUsuario;
import com.br.startup.tolevBack.users.internal.enums.SituacaoFinanceira;
import com.br.startup.tolevBack.users.internal.enums.TipoEmprego;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String genero;
    private LocalDate dataNascimento;
    private String nomeUsuario;
    private String senha;
    private String email;
    @Column(name = "receber_email")
    private Boolean receberEmail;

    // ----- Perfil financeiro (coletado no onboarding) -----

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo_principal")
    private ObjetivoPrincipal objetivoPrincipal;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_financeira")
    private SituacaoFinanceira situacaoFinanceira;

    @Enumerated(EnumType.STRING)
    @Column(name = "ocupacao")
    private TipoEmprego ocupacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel")
    @Builder.Default
    private PapelUsuario papel = PapelUsuario.USER;

    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<UsuarioAssinatura> usuarioAssinaturas;

    @PrePersist
    void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    @PreUpdate
    void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
