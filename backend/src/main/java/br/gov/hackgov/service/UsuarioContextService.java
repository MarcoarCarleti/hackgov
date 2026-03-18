package br.gov.hackgov.service;

import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.repository.UsuarioRepository;
import br.gov.hackgov.security.SecurityUtils;
import br.gov.hackgov.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class UsuarioContextService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioContextService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario usuarioAtual() {
        UserPrincipal principal = SecurityUtils.currentUser();
        return usuarioRepository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
