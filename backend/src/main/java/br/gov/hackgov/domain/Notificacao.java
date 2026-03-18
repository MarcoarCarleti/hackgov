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
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificacaoTipo tipo;

    @Column(nullable = false)
    private OffsetDateTime dataEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificacaoStatus status;

    @Column(nullable = false, length = 500)
    private String conteudo;
}
