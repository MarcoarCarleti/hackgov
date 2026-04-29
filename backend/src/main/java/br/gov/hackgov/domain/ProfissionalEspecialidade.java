package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "profissional_especialidade", uniqueConstraints = {
        @UniqueConstraint(name = "uk_profissional_especialidade", columnNames = {"profissional_id", "especialidade_id"})
})
public class ProfissionalEspecialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profissional_especialidade_profissional"))
    private Medico profissional;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profissional_especialidade_especialidade"))
    private Especialidade especialidade;

    @Column(name = "principal", nullable = false)
    private boolean principal = false;

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
