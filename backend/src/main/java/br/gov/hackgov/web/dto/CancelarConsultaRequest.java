package br.gov.hackgov.web.dto;

import jakarta.validation.constraints.Size;

public record CancelarConsultaRequest(
        @Size(max = 250) String motivo
) {
}
