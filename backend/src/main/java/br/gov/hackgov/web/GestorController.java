package br.gov.hackgov.web;

import br.gov.hackgov.security.SecurityUtils;
import br.gov.hackgov.service.CatalogoService;
import br.gov.hackgov.service.ConsultaService;
import br.gov.hackgov.service.DashboardService;
import br.gov.hackgov.service.FilaEsperaService;
import br.gov.hackgov.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class GestorController {

    private final DashboardService dashboardService;
    private final ConsultaService consultaService;
    private final CatalogoService catalogoService;
    private final FilaEsperaService filaEsperaService;

    public GestorController(DashboardService dashboardService,
                            ConsultaService consultaService,
                            CatalogoService catalogoService,
                            FilaEsperaService filaEsperaService) {
        this.dashboardService = dashboardService;
        this.consultaService = consultaService;
        this.catalogoService = catalogoService;
        this.filaEsperaService = filaEsperaService;
    }

    @GetMapping("/dashboard/resumo")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public DashboardResumoResponse resumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.resumo(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/dashboard/agendamentos")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<IndicadorDiaResponse> agendamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.agendamentosPorDia(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/dashboard/faltas")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<IndicadorDiaResponse> faltas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.faltasPorDia(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/dashboard/cancelamentos")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<IndicadorDiaResponse> cancelamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.cancelamentosPorDia(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/dashboard/reaproveitamento")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<ReaproveitamentoResponse> reaproveitamento(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.reaproveitamentoPorDia(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/dashboard/ranking-medicos")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<RankingMedicoResponse> rankingMedicos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId
    ) {
        return dashboardService.rankingMedicos(inicio, fim, ubsId);
    }

    @GetMapping("/dashboard/serie-agendamentos-faltas")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<SerieAgendamentoFaltaResponse> serie(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long ubsId,
            @RequestParam(required = false) Long medicoId
    ) {
        return dashboardService.serieAgendamentosEFaltas(inicio, fim, ubsId, medicoId);
    }

    @GetMapping("/consultas")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<ConsultaResponse> listarConsultas(@RequestParam(required = false) Long ubsId) {
        return consultaService.listarConsultasParaGestor(ubsId);
    }

    @PatchMapping("/consultas/{id}/status")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public ConsultaResponse atualizarStatus(@PathVariable Long id,
                                            @Valid @RequestBody AtualizarStatusConsultaRequest request) {
        return consultaService.atualizarStatusGestor(id, request, SecurityUtils.currentUser().getId());
    }

    @GetMapping("/medicos")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<MedicoResponse> medicos() {
        return catalogoService.listarMedicos();
    }

    @GetMapping("/ubs")
    public List<UbsResponse> ubs() {
        return catalogoService.listarUbs();
    }

    @GetMapping("/fila-espera")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<FilaEsperaResponse> filaEspera() {
        return filaEsperaService.listarTodas();
    }
}
