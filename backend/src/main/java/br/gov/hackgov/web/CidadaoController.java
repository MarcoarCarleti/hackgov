package br.gov.hackgov.web;

import br.gov.hackgov.service.AgendaService;
import br.gov.hackgov.service.ConsultaService;
import br.gov.hackgov.service.FilaEsperaService;
import br.gov.hackgov.service.NotificacaoService;
import br.gov.hackgov.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CidadaoController {

    private final ConsultaService consultaService;
    private final AgendaService agendaService;
    private final FilaEsperaService filaEsperaService;
    private final NotificacaoService notificacaoService;

    public CidadaoController(ConsultaService consultaService,
                             AgendaService agendaService,
                             FilaEsperaService filaEsperaService,
                             NotificacaoService notificacaoService) {
        this.consultaService = consultaService;
        this.agendaService = agendaService;
        this.filaEsperaService = filaEsperaService;
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/me/consultas")
    @PreAuthorize("hasRole('CIDADAO')")
    public List<ConsultaResponse> minhasConsultas() {
        return consultaService.minhasConsultas();
    }

    @PostMapping("/consultas")
    @PreAuthorize("hasRole('CIDADAO')")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse criarConsulta(@Valid @RequestBody CriarConsultaRequest request) {
        return consultaService.criar(request);
    }

    @PostMapping("/consultas/{id}/cancelar")
    @PreAuthorize("hasRole('CIDADAO')")
    public ConsultaResponse cancelarConsulta(@PathVariable Long id,
                                             @Valid @RequestBody(required = false) CancelarConsultaRequest request) {
        CancelarConsultaRequest payload = request == null ? new CancelarConsultaRequest(null) : request;
        return consultaService.cancelarMinhaConsulta(id, payload);
    }

    @GetMapping("/agenda/disponivel")
    @PreAuthorize("hasRole('CIDADAO')")
    public List<AgendaDisponivelResponse> agendaDisponivel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return agendaService.agendaDisponivel(dataInicial, dataFinal);
    }

    @PostMapping("/fila-espera")
    @PreAuthorize("hasRole('CIDADAO')")
    @ResponseStatus(HttpStatus.CREATED)
    public FilaEsperaResponse entrarFilaEspera(@Valid @RequestBody CriarFilaEsperaRequest request) {
        return filaEsperaService.criar(request);
    }

    @GetMapping("/notificacoes")
    @PreAuthorize("hasRole('CIDADAO')")
    public List<NotificacaoResponse> minhasNotificacoes() {
        return notificacaoService.minhasNotificacoes();
    }
}
