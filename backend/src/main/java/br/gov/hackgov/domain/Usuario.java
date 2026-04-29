package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf"),
        @UniqueConstraint(name = "uk_usuario_cartao_sus", columnNames = "cartao_sus"),
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "cartao_sus", nullable = false, length = 15)
    private String cartaoSus;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private Role role = Role.PACIENTE;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ubs_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_ubs"))
    private Ubs ubsReferencia;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
