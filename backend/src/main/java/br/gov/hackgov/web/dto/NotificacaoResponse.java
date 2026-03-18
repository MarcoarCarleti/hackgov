package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.NotificacaoStatus;
import br.gov.hackgov.domain.NotificacaoTipo;

import java.time.OffsetDateTime;

public record NotificacaoResponse(
        Long id,
        Long consultaId,
        NotificacaoTipo tipo,
        OffsetDateTime dataEnvio,
        NotificacaoStatus status,
        String conteudo
) {
}
