package br.gov.hackgov.web;

import br.gov.hackgov.service.RiscoFaltasService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiscoFaltasController {
    private final RiscoFaltasService service;

    public RiscoFaltasController(RiscoFaltasService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/risco-faltas")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public RiscoFaltasService.ResultadoRisco risco(@RequestParam(required = false) Long ubsId) {
        return service.analisar(ubsId);
    }
}
