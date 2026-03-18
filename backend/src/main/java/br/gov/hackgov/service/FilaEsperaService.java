package br.gov.hackgov.service;

import br.gov.hackgov.domain.FilaEspera;
import br.gov.hackgov.domain.Medico;
import br.gov.hackgov.domain.Ubs;
import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.exception.BusinessException;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.repository.FilaEsperaRepository;
import br.gov.hackgov.repository.MedicoRepository;
import br.gov.hackgov.repository.UbsRepository;
import br.gov.hackgov.web.dto.CriarFilaEsperaRequest;
import br.gov.hackgov.web.dto.FilaEsperaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FilaEsperaService {

    private final FilaEsperaRepository filaEsperaRepository;
    private final UbsRepository ubsRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioContextService usuarioContextService;
    private final AuditService auditService;

    public FilaEsperaService(FilaEsperaRepository filaEsperaRepository,
                             UbsRepository ubsRepository,
                             MedicoRepository medicoRepository,
                             UsuarioContextService usuarioContextService,
                             AuditService auditService) {
        this.filaEsperaRepository = filaEsperaRepository;
        this.ubsRepository = ubsRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioContextService = usuarioContextService;
        this.auditService = auditService;
    }

    @Transactional
    public FilaEsperaResponse criar(CriarFilaEsperaRequest request) {
        Usuario usuario = usuarioContextService.usuarioAtual();

        if (!usuario.getUbsReferencia().getId().equals(request.ubsId())) {
            throw new BusinessException("A fila de espera deve ser da UBS de referência do cidadão");
        }

        Ubs ubs = ubsRepository.findById(request.ubsId())
                .orElseThrow(() -> new NotFoundException("UBS não encontrada"));

        Medico medico = null;
        if (request.medicoId() != null) {
            medico = medicoRepository.findById(request.medicoId())
                    .orElseThrow(() -> new NotFoundException("Médico não encontrado"));
            if (!medico.getUbs().getId().equals(ubs.getId())) {
                throw new BusinessException("Médico não pertence à UBS informada");
            }
        }

        FilaEspera fila = new FilaEspera();
        fila.setUsuario(usuario);
        fila.setUbs(ubs);
        fila.setMedico(medico);
        fila.setDataDesejada(request.dataDesejada());
        fila.setPrioridade(request.prioridade() == null ? 100 : request.prioridade());

        fila = filaEsperaRepository.save(fila);
        auditService.registrar("FILA_ESPERA", fila.getId(), "CRIACAO", usuario.getId(), "Inclusão em fila de espera");
        return toResponse(fila);
    }

    public List<FilaEsperaResponse> minhasEntradas() {
        Usuario usuario = usuarioContextService.usuarioAtual();
        return filaEsperaRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<FilaEsperaResponse> listarTodas() {
        return filaEsperaRepository.findAll().stream().map(this::toResponse).toList();
    }

    private FilaEsperaResponse toResponse(FilaEspera fila) {
        return new FilaEsperaResponse(
                fila.getId(),
                fila.getUsuario().getId(),
                fila.getUsuario().getNome(),
                fila.getUbs().getId(),
                fila.getUbs().getNome(),
                fila.getMedico() == null ? null : fila.getMedico().getId(),
                fila.getMedico() == null ? null : fila.getMedico().getNome(),
                fila.getDataDesejada(),
                fila.getStatus(),
                fila.getPrioridade(),
                fila.getCreatedAt()
        );
    }
}
