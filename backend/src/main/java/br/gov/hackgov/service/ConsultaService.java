package br.gov.hackgov.service;

import br.gov.hackgov.domain.*;
import br.gov.hackgov.exception.BusinessException;
import br.gov.hackgov.exception.NotFoundException;
import br.gov.hackgov.repository.*;
import br.gov.hackgov.web.dto.AtualizarStatusConsultaRequest;
import br.gov.hackgov.web.dto.CancelarConsultaRequest;
import br.gov.hackgov.web.dto.ConsultaResponse;
import br.gov.hackgov.web.dto.CriarConsultaRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultaService {

    private static final List<ConsultaStatus> STATUS_ATIVOS = List.of(ConsultaStatus.AGENDADA, ConsultaStatus.ENCAIXADA, ConsultaStatus.REAGENDADA);

    private final ConsultaRepository consultaRepository;
    private final AgendaSlotRepository agendaSlotRepository;
    private final FeriadoRepository feriadoRepository;
    private final UsuarioContextService usuarioContextService;
    private final FilaEsperaRepository filaEsperaRepository;
    private final AuditService auditService;

    public ConsultaService(ConsultaRepository consultaRepository,
                           AgendaSlotRepository agendaSlotRepository,
                           FeriadoRepository feriadoRepository,
                           UsuarioContextService usuarioContextService,
                           FilaEsperaRepository filaEsperaRepository,
                           AuditService auditService) {
        this.consultaRepository = consultaRepository;
        this.agendaSlotRepository = agendaSlotRepository;
        this.feriadoRepository = feriadoRepository;
        this.usuarioContextService = usuarioContextService;
        this.filaEsperaRepository = filaEsperaRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ConsultaResponse criar(CriarConsultaRequest request) {
        Usuario usuario = usuarioContextService.usuarioAtual();
        AgendaSlot slot = agendaSlotRepository.findById(request.agendaSlotId())
                .orElseThrow(() -> new NotFoundException("Slot não encontrado"));

        validarRegrasAgendamento(usuario, slot);

        Consulta consulta = new Consulta();
        consulta.setUsuario(usuario);
        consulta.setMedico(slot.getMedico());
        consulta.setUbs(slot.getUbs());
        consulta.setAgendaSlot(slot);
        consulta.setStatus(ConsultaStatus.AGENDADA);
        consulta.setDataConsulta(slot.getData());
        consulta.setHoraConsulta(slot.getHoraInicio());
        consulta.setObservacoes(request.observacoes());
        consulta = consultaRepository.save(consulta);

        slot.setDisponivel(false);
        agendaSlotRepository.save(slot);

        auditService.registrar("CONSULTA", consulta.getId(), "CRIACAO", usuario.getId(),
                "Consulta criada para " + consulta.getDataConsulta() + " " + consulta.getHoraConsulta());
        return toResponse(consulta);
    }

    public List<ConsultaResponse> minhasConsultas() {
        Usuario usuario = usuarioContextService.usuarioAtual();
        return consultaRepository.findByUsuarioIdOrderByDataConsultaDescHoraConsultaDesc(usuario.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ConsultaResponse cancelarMinhaConsulta(Long consultaId, CancelarConsultaRequest request) {
        Usuario usuario = usuarioContextService.usuarioAtual();
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada"));

        if (!consulta.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Consulta não pertence ao usuário logado");
        }

        cancelarConsultaInterno(consulta, ConsultaStatus.CANCELADA_PELO_PACIENTE, request.motivo(), usuario.getId());
        return toResponse(consulta);
    }

    public List<ConsultaResponse> listarConsultasParaGestor(Long ubsId) {
        List<Consulta> consultas = ubsId == null
                ? consultaRepository.findAllByOrderByDataConsultaDescHoraConsultaDesc()
                : consultaRepository.findByUbsIdOrderByDataConsultaDescHoraConsultaDesc(ubsId);
        return consultas.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ConsultaResponse atualizarStatusGestor(Long id, AtualizarStatusConsultaRequest request, Long usuarioResponsavelId) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta não encontrada"));

        consulta.setStatus(request.status());
        consulta.setObservacoes(request.observacoes());

        if (request.status() == ConsultaStatus.CANCELADA_PELO_SISTEMA) {
            cancelarConsultaInterno(consulta, ConsultaStatus.CANCELADA_PELO_SISTEMA, request.observacoes(), usuarioResponsavelId);
            return toResponse(consulta);
        }

        consultaRepository.save(consulta);
        auditService.registrar("CONSULTA", consulta.getId(), "ALTERACAO_STATUS", usuarioResponsavelId,
                "Status alterado para " + request.status());
        return toResponse(consulta);
    }

    @Transactional
    public void processarEncaixes() {
        List<AgendaSlot> slotsDisponiveis = agendaSlotRepository.findByDisponivelTrueAndDataGreaterThanEqualOrderByDataAscHoraInicioAsc(LocalDate.now());
        for (AgendaSlot slot : slotsDisponiveis) {
            tentarEncaixeAutomatico(slot, null);
        }
    }

    private void validarRegrasAgendamento(Usuario usuario, AgendaSlot slot) {
        LocalDate hoje = LocalDate.now();
        if (!slot.isDisponivel()) {
            throw new BusinessException("Horário não está mais disponível");
        }
        if (!slot.getUbs().getId().equals(usuario.getUbsReferencia().getId())) {
            throw new BusinessException("O cidadão só pode agendar na UBS de referência");
        }
        if (slot.getData().isBefore(hoje)) {
            throw new BusinessException("Não é permitido agendar em data passada");
        }
        if (feriadoRepository.existsByData(slot.getData())) {
            throw new BusinessException("Não é permitido agendar em feriados");
        }

        boolean conflito = consultaRepository.existsByUsuarioIdAndDataConsultaAndHoraConsultaAndStatusIn(
                usuario.getId(),
                slot.getData(),
                slot.getHoraInicio(),
                STATUS_ATIVOS
        );
        if (conflito) {
            throw new BusinessException("Paciente já possui consulta ativa nesse horário");
        }
    }

    private void cancelarConsultaInterno(Consulta consulta,
                                         ConsultaStatus statusCancelamento,
                                         String motivo,
                                         Long usuarioResponsavelId) {
        if (!STATUS_ATIVOS.contains(consulta.getStatus())) {
            throw new BusinessException("Apenas consultas ativas podem ser canceladas");
        }

        LocalDateTime horarioConsulta = LocalDateTime.of(consulta.getDataConsulta(), consulta.getHoraConsulta());
        if (horarioConsulta.isBefore(LocalDateTime.now().plusHours(12))) {
            throw new BusinessException("Cancelamento permitido somente com 12h de antecedência");
        }

        consulta.setStatus(statusCancelamento);
        consulta.setCanceladoEm(OffsetDateTime.now());
        consulta.setMotivoCancelamento(motivo);
        consultaRepository.save(consulta);

        AgendaSlot slot = consulta.getAgendaSlot();
        slot.setDisponivel(true);
        agendaSlotRepository.save(slot);

        auditService.registrar("CONSULTA", consulta.getId(), "CANCELAMENTO", usuarioResponsavelId,
                "Consulta cancelada e vaga liberada");

        tentarEncaixeAutomatico(slot, usuarioResponsavelId);
    }

    private void tentarEncaixeAutomatico(AgendaSlot slot, Long usuarioResponsavelId) {
        List<FilaEspera> candidatos = filaEsperaRepository
                .findByUbsIdAndStatusAndDataDesejadaLessThanEqualAndMedicoIdOrderByPrioridadeAscCreatedAtAsc(
                        slot.getUbs().getId(),
                        FilaEsperaStatus.ATIVA,
                        slot.getData(),
                        slot.getMedico().getId()
                );
        candidatos.addAll(filaEsperaRepository
                .findByUbsIdAndStatusAndDataDesejadaLessThanEqualAndMedicoIsNullOrderByPrioridadeAscCreatedAtAsc(
                        slot.getUbs().getId(),
                        FilaEsperaStatus.ATIVA,
                        slot.getData()
                ));

        for (FilaEspera fila : candidatos) {
            boolean conflito = !consultaRepository.findConflitos(
                    fila.getUsuario().getId(),
                    slot.getData(),
                    slot.getHoraInicio(),
                    -1L
            ).isEmpty();
            if (conflito) {
                continue;
            }

            Consulta encaixe = new Consulta();
            encaixe.setUsuario(fila.getUsuario());
            encaixe.setMedico(slot.getMedico());
            encaixe.setUbs(slot.getUbs());
            encaixe.setAgendaSlot(slot);
            encaixe.setStatus(ConsultaStatus.ENCAIXADA);
            encaixe.setDataConsulta(slot.getData());
            encaixe.setHoraConsulta(slot.getHoraInicio());
            encaixe.setObservacoes("Encaixe automático por vaga liberada");
            encaixe.setEncaixeAutomatico(true);
            consultaRepository.save(encaixe);

            fila.setStatus(FilaEsperaStatus.CONVERTIDA_EM_ENCAIXE);
            filaEsperaRepository.save(fila);

            slot.setDisponivel(false);
            agendaSlotRepository.save(slot);

            auditService.registrar("CONSULTA", encaixe.getId(), "ENCAIXE_AUTOMATICO", usuarioResponsavelId,
                    "Encaixe automático processado para usuário " + fila.getUsuario().getId());
            break;
        }
    }

    public ConsultaResponse toResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getUsuario().getId(),
                consulta.getUsuario().getNome(),
                consulta.getMedico().getId(),
                consulta.getMedico().getNome(),
                consulta.getMedico().getEspecialidade(),
                consulta.getUbs().getId(),
                consulta.getUbs().getNome(),
                consulta.getAgendaSlot().getId(),
                consulta.getStatus(),
                consulta.getDataConsulta(),
                consulta.getHoraConsulta(),
                consulta.getObservacoes(),
                consulta.getCriadoEm(),
                consulta.getCanceladoEm(),
                consulta.isEncaixeAutomatico()
        );
    }
}
