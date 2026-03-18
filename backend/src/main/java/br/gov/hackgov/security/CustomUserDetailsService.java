package br.gov.hackgov.security;

import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        return new UserPrincipal(usuario);
    }
}
