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
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ubs_id")
    private Ubs ubs;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_slot_id")
    private AgendaSlot agendaSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultaStatus status;

    @Column(nullable = false)
    private LocalDate dataConsulta;

    @Column(nullable = false)
    private LocalTime horaConsulta;

    private String observacoes;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    private OffsetDateTime canceladoEm;

    private String motivoCancelamento;

    @Column(nullable = false)
    private boolean encaixeAutomatico = false;

    @PrePersist
    public void onCreate() {
        this.criadoEm = OffsetDateTime.now();
    }
}
