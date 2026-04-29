package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "fila_espera")
public class FilaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fila_espera_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ubs_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fila_espera_ubs"))
    private Ubs ubs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", foreignKey = @ForeignKey(name = "fk_fila_espera_profissional"))
    private Medico medico;

    @Column(name = "data_desejada", nullable = false)
    private LocalDate dataDesejada;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private FilaEsperaStatus status = FilaEsperaStatus.ATIVA;

    @Column(name = "prioridade", nullable = false)
    private int prioridade = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
