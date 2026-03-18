package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.ConsultaStatus;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusConsultaRequest(
        @NotNull ConsultaStatus status,
        String observacoes
) {
}
