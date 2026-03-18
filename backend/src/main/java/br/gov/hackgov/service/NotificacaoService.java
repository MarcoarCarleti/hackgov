package br.gov.hackgov.service;

import br.gov.hackgov.domain.*;
import br.gov.hackgov.repository.ConsultaRepository;
import br.gov.hackgov.repository.NotificacaoRepository;
import br.gov.hackgov.web.dto.NotificacaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioContextService usuarioContextService;
    private final AuditService auditService;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                             ConsultaRepository consultaRepository,
                             UsuarioContextService usuarioContextService,
                             AuditService auditService) {
        this.notificacaoRepository = notificacaoRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioContextService = usuarioContextService;
        this.auditService = auditService;
    }

    @Transactional
    public int processarAlertas() {
        List<Consulta> futuras = consultaRepository.findFuturasAtivas(LocalDate.now(), LocalTime.now());
        int enviadas = 0;

        for (Consulta consulta : futuras) {
            LocalDateTime horarioConsulta = LocalDateTime.of(consulta.getDataConsulta(), consulta.getHoraConsulta());
            long horas = Duration.between(LocalDateTime.now(), horarioConsulta).toHours();

            if (horas <= 48 && horas > 47) {
                enviadas += enviarSeNecessario(consulta, NotificacaoTipo.LEMBRETE_48H);
            }
            if (horas <= 24 && horas > 23) {
                enviadas += enviarSeNecessario(consulta, NotificacaoTipo.LEMBRETE_24H);
            }
        }

        return enviadas;
    }

    public List<NotificacaoResponse> minhasNotificacoes() {
        Usuario usuario = usuarioContextService.usuarioAtual();
        return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuario.getId())
                .stream()
                .map(n -> new NotificacaoResponse(
                        n.getId(),
                        n.getConsulta().getId(),
                        n.getTipo(),
                        n.getDataEnvio(),
                        n.getStatus(),
                        n.getConteudo()
                ))
                .toList();
    }

    private int enviarSeNecessario(Consulta consulta, NotificacaoTipo tipo) {
        if (notificacaoRepository.existsByConsultaIdAndTipo(consulta.getId(), tipo)) {
            return 0;
        }

        String conteudo = String.format(
                "Lembrete: consulta em %s às %s com Dr(a). %s na UBS %s.",
                consulta.getDataConsulta(),
                consulta.getHoraConsulta(),
                consulta.getMedico().getNome(),
                consulta.getUbs().getNome()
        );

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(consulta.getUsuario());
        notificacao.setConsulta(consulta);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(OffsetDateTime.now());
        notificacao.setStatus(NotificacaoStatus.ENVIADA);
        notificacao.setConteudo(conteudo);
        notificacaoRepository.save(notificacao);

        log.info("[NOTIFICACAO_SIMULADA] usuario={} consulta={} tipo={} msg={}",
                consulta.getUsuario().getId(), consulta.getId(), tipo, conteudo);

        auditService.registrar("NOTIFICACAO", notificacao.getId(), "ENVIO_ALERTA", null,
                "Envio de alerta " + tipo + " para consulta " + consulta.getId());
        return 1;
    }
}
