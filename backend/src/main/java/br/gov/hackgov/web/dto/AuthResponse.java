package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.Role;

public record AuthResponse(
        String token,
        Long usuarioId,
        String nome,
        Role role
) {
}
