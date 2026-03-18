package br.gov.hackgov.web.dto;

public record UbsResponse(
        Long id,
        String nome,
        String endereco,
        String cidade,
        String estado,
        String telefone
) {
}
