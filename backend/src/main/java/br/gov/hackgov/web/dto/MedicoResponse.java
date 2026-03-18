package br.gov.hackgov.web.dto;

public record MedicoResponse(
        Long id,
        String nome,
        String especialidade,
        Long ubsId,
        String ubsNome,
        boolean ativo
) {
}
