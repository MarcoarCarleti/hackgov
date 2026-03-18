package br.gov.hackgov.service;

import br.gov.hackgov.domain.AgendaSlot;
import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.repository.AgendaSlotRepository;
import br.gov.hackgov.web.dto.AgendaDisponivelResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgendaService {

    private final AgendaSlotRepository agendaSlotRepository;
    private final UsuarioContextService usuarioContextService;

    public AgendaService(AgendaSlotRepository agendaSlotRepository, UsuarioContextService usuarioContextService) {
        this.agendaSlotRepository = agendaSlotRepository;
        this.usuarioContextService = usuarioContextService;
    }

    public List<AgendaDisponivelResponse> agendaDisponivel(LocalDate dataInicial, LocalDate dataFinal) {
        Usuario usuario = usuarioContextService.usuarioAtual();
        LocalDate inicio = dataInicial == null ? LocalDate.now() : dataInicial;
        LocalDate fim = dataFinal == null ? inicio.plusDays(14) : dataFinal;

        List<AgendaSlot> slots = agendaSlotRepository
                .findByUbsIdAndDataBetweenAndDisponivelTrueOrderByDataAscHoraInicioAsc(
                        usuario.getUbsReferencia().getId(),
                        inicio,
                        fim
                );

        return slots.stream().map(slot -> new AgendaDisponivelResponse(
                slot.getId(),
                slot.getMedico().getId(),
                slot.getMedico().getNome(),
                slot.getMedico().getEspecialidade(),
                slot.getUbs().getId(),
                slot.getUbs().getNome(),
                slot.getData(),
                slot.getHoraInicio(),
                slot.getHoraFim()
        )).toList();
    }
}
