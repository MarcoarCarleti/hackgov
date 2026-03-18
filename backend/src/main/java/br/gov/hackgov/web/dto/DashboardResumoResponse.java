package br.gov.hackgov.web.dto;

public record DashboardResumoResponse(
        long totalAgendamentos,
        long totalRealizadas,
        long totalFaltas,
        long totalCancelamentos,
        double taxaFaltas,
        double taxaCancelamentos,
        long vagasReaproveitadas
) {
}
