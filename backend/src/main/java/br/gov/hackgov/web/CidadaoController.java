package br.gov.hackgov.web;

import br.gov.hackgov.service.AgendaService;
import br.gov.hackgov.service.ConsultaService;
import br.gov.hackgov.service.FilaEsperaService;
import br.gov.hackgov.service.NotificacaoService;
import br.gov.hackgov.web.dto.AgendaDisponivelResponse;
import br.gov.hackgov.web.dto.CancelarConsultaRequest;
import br.gov.hackgov.web.dto.ConsultaResponse;
import br.gov.hackgov.web.dto.CriarConsultaRequest;
import br.gov.hackgov.web.dto.CriarFilaEsperaRequest;
import br.gov.hackgov.web.dto.FilaEsperaResponse;
import br.gov.hackgov.web.dto.NotificacaoResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasRole('PACIENTE')")
    public List<ConsultaResponse> minhasConsultas() {
        return consultaService.minhasConsultas();
    }

    @PostMapping("/consultas")
    @PreAuthorize("hasRole('PACIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse criarConsulta(@Valid @RequestBody CriarConsultaRequest request) {
        return consultaService.criar(request);
    }

    @PostMapping("/consultas/{id}/cancelar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ConsultaResponse cancelarConsulta(@PathVariable Long id,
                                             @Valid @RequestBody(required = false) CancelarConsultaRequest request) {
        CancelarConsultaRequest payload = request == null ? new CancelarConsultaRequest(null) : request;
        return consultaService.cancelarMinhaConsulta(id, payload);
    }

    @GetMapping("/agenda/disponivel")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<AgendaDisponivelResponse> agendaDisponivel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return agendaService.agendaDisponivel(dataInicial, dataFinal);
    }

    @PostMapping("/fila-espera")
    @PreAuthorize("hasRole('PACIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public FilaEsperaResponse entrarFilaEspera(@Valid @RequestBody CriarFilaEsperaRequest request) {
        return filaEsperaService.criar(request);
    }

    @GetMapping("/notificacoes")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<NotificacaoResponse> minhasNotificacoes() {
        return notificacaoService.minhasNotificacoes();
    }
}
