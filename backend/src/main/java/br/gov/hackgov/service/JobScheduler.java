package br.gov.hackgov.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    private final NotificacaoService notificacaoService;
    private final ConsultaService consultaService;

    public JobScheduler(NotificacaoService notificacaoService, ConsultaService consultaService) {
        this.notificacaoService = notificacaoService;
        this.consultaService = consultaService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void processarAlertasHorario() {
        notificacaoService.processarAlertas();
    }

    @Scheduled(cron = "0 30 * * * *")
    public void processarEncaixesHorario() {
        consultaService.processarEncaixes();
    }
}
