package br.gov.hackgov.web.dto;

import br.gov.hackgov.domain.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotBlank @Size(min = 15, max = 15) String cartaoSus,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank @Size(min = 6) String senha,
        @NotNull Long ubsReferenciaId,
        Role role
) {
}
