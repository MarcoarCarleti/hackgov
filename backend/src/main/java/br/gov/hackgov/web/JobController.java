package br.gov.hackgov.web;

import br.gov.hackgov.service.ConsultaService;
import br.gov.hackgov.service.NotificacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final NotificacaoService notificacaoService;
    private final ConsultaService consultaService;

    public JobController(NotificacaoService notificacaoService, ConsultaService consultaService) {
        this.notificacaoService = notificacaoService;
        this.consultaService = consultaService;
    }

    @PostMapping("/processar-alertas")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public Map<String, Object> processarAlertas() {
        int enviados = notificacaoService.processarAlertas();
        return Map.of("status", "ok", "alertasEnviados", enviados);
    }

    @PostMapping("/processar-encaixes")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public Map<String, Object> processarEncaixes() {
        consultaService.processarEncaixes();
        return Map.of("status", "ok");
    }
}
