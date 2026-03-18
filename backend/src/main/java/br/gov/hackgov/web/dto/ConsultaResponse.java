package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.ConsultaStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record ConsultaResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long medicoId,
        String medicoNome,
        String especialidade,
        Long ubsId,
        String ubsNome,
        Long agendaSlotId,
        ConsultaStatus status,
        LocalDate dataConsulta,
        LocalTime horaConsulta,
        String observacoes,
        OffsetDateTime criadoEm,
        OffsetDateTime canceladoEm,
        boolean encaixeAutomatico
) {
}
