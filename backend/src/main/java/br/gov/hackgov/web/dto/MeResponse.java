package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.Role;

public record MeResponse(
        Long id,
        String nome,
        String cpfMascarado,
        String cartaoSusMascarado,
        String email,
        String telefone,
        Role role,
        Long ubsReferenciaId,
        String ubsReferenciaNome
) {
}
