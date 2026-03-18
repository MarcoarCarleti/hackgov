package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.FilaEsperaStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record FilaEsperaResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long ubsId,
        String ubsNome,
        Long medicoId,
        String medicoNome,
        LocalDate dataDesejada,
        FilaEsperaStatus status,
        int prioridade,
        OffsetDateTime createdAt
) {
}
