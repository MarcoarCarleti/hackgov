package br.gov.hackgov.service;

import br.gov.hackgov.domain.Role;
import br.gov.hackgov.domain.Ubs;
import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.exception.BusinessException;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.repository.UbsRepository;
import br.gov.hackgov.repository.UsuarioRepository;
import br.gov.hackgov.security.JwtService;
import br.gov.hackgov.security.UserPrincipal;
import br.gov.hackgov.util.CpfValidator;
import br.gov.hackgov.util.MaskingUtils;
import br.gov.hackgov.web.dto.AuthResponse;
import br.gov.hackgov.web.dto.LoginRequest;
import br.gov.hackgov.web.dto.MeResponse;
import br.gov.hackgov.web.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UbsRepository ubsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;
    private final UsuarioContextService usuarioContextService;

    public AuthService(UsuarioRepository usuarioRepository,
                       UbsRepository ubsRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       AuditService auditService,
                       UsuarioContextService usuarioContextService) {
        this.usuarioRepository = usuarioRepository;
        this.ubsRepository = ubsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditService = auditService;
        this.usuarioContextService = usuarioContextService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cpf = sanitizeDigits(request.cpf());
        String cartaoSus = sanitizeDigits(request.cartaoSus());

        if (!CpfValidator.isValid(cpf)) {
            throw new BusinessException("CPF inválido");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new BusinessException("CPF já cadastrado");
        }
        if (usuarioRepository.existsByCartaoSus(cartaoSus)) {
            throw new BusinessException("Cartão SUS já cadastrado");
        }

        Ubs ubs = ubsRepository.findById(request.ubsReferenciaId())
                .orElseThrow(() -> new NotFoundException("UBS não encontrada"));

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setCpf(cpf);
        usuario.setCartaoSus(cartaoSus);
        usuario.setEmail(request.email().toLowerCase());
        usuario.setTelefone(request.telefone());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setRole(Role.PACIENTE);
        usuario.setUbsReferencia(ubs);

        usuario = usuarioRepository.save(usuario);
        auditService.registrar("USUARIO", usuario.getId(), "CADASTRO", usuario.getId(), "Cadastro de paciente");

        UserPrincipal principal = new UserPrincipal(usuario);
        return new AuthResponse(jwtService.generateToken(principal), usuario.getId(), usuario.getNome(), usuario.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        UserPrincipal principal = new UserPrincipal(usuario);
        return new AuthResponse(jwtService.generateToken(principal), usuario.getId(), usuario.getNome(), usuario.getRole());
    }

    public MeResponse me() {
        Usuario usuario = usuarioContextService.usuarioAtual();
        return new MeResponse(
                usuario.getId(),
                usuario.getNome(),
                MaskingUtils.maskCpf(usuario.getCpf()),
                MaskingUtils.maskCartaoSus(usuario.getCartaoSus()),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getUbsReferencia().getId(),
                usuario.getUbsReferencia().getNome()
        );
    }

    private String sanitizeDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
