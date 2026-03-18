package br.gov.hackgov.web.dto;

import jakarta.validation.constraints.NotNull;

public record CriarConsultaRequest(
        @NotNull Long agendaSlotId,
        String observacoes
) {
}
