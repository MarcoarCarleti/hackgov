package br.gov.hackgov.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notificacao_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notificacao_agendamento"))
    private Consulta consulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private NotificacaoTipo tipo;

    @Column(name = "data_envio", nullable = false)
    private OffsetDateTime dataEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private NotificacaoStatus status;

    @Column(name = "conteudo", nullable = false, length = 500)
    private String conteudo;
}
