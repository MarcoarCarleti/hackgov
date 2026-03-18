package br.gov.hackgov.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CriarFilaEsperaRequest(
        @NotNull Long ubsId,
        Long medicoId,
        @NotNull LocalDate dataDesejada,
        Integer prioridade
) {
}
