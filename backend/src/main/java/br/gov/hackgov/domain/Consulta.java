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
@Table(name = "agendamento")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_agendamento_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_profissional_id", nullable = false, foreignKey = @ForeignKey(name = "fk_agendamento_agenda_profissional"))
    private AgendaSlot agendaSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConsultaStatus status;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "cancelado_em")
    private OffsetDateTime canceladoEm;

    @Column(name = "motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    @Column(name = "encaixe_automatico", nullable = false)
    private boolean encaixeAutomatico = false;

    @Transient
    public Medico getMedico() {
        return agendaSlot == null ? null : agendaSlot.getMedico();
    }

    @Transient
    public Ubs getUbs() {
        return agendaSlot == null ? null : agendaSlot.getUbs();
    }

    @Transient
    public Especialidade getEspecialidade() {
        return agendaSlot == null ? null : agendaSlot.getEspecialidade();
    }

    @Transient
    public LocalDate getDataConsulta() {
        return agendaSlot == null ? null : agendaSlot.getData();
    }

    @Transient
    public LocalTime getHoraConsulta() {
        return agendaSlot == null ? null : agendaSlot.getHoraInicio();
    }

    @PrePersist
    public void onCreate() {
        this.criadoEm = OffsetDateTime.now();
    }
}
