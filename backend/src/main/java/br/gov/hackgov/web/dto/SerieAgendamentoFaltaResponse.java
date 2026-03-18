package br.gov.hackgov.web.dto;

public record SerieAgendamentoFaltaResponse(
        String dia,
        long agendamentos,
        long faltas
) {
}
