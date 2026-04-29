package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "agenda_profissional", uniqueConstraints = {
        @UniqueConstraint(name = "uk_agenda_profissional_profissional_data_hora", columnNames = {"profissional_id", "data_agenda", "hora_inicio"})
})
public class AgendaSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false, foreignKey = @ForeignKey(name = "fk_agenda_profissional_profissional"))
    private Medico medico;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = false, foreignKey = @ForeignKey(name = "fk_agenda_profissional_especialidade"))
    private Especialidade especialidade;

    @Column(name = "data_agenda", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "disponivel", nullable = false)
    private boolean disponivel = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Transient
    public Ubs getUbs() {
        return medico == null ? null : medico.getUbs();
    }

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
