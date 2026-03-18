package br.gov.hackgov.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendaDisponivelResponse(
        Long slotId,
        Long medicoId,
        String medicoNome,
        String especialidade,
        Long ubsId,
        String ubsNome,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim
) {
}
