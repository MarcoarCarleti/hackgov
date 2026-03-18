package br.gov.hackgov.integration;

import br.gov.hackgov.domain.*;
import br.gov.hackgov.exception.BusinessException;
import br.gov.hackgov.repository.*;
import br.gov.hackgov.security.UserPrincipal;
import br.gov.hackgov.service.ConsultaService;
import br.gov.hackgov.web.dto.CancelarConsultaRequest;
import br.gov.hackgov.web.dto.ConsultaResponse;
import br.gov.hackgov.web.dto.CriarConsultaRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConsultaServiceRulesTest {

    @Autowired
    private ConsultaService consultaService;
    @Autowired
    private UbsRepository ubsRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private AgendaSlotRepository agendaSlotRepository;
    @Autowired
    private FeriadoRepository feriadoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private FilaEsperaRepository filaEsperaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveFalharAgendamentoDataPassada() {
        Fixture fixture = fixtureBase(LocalDate.now().minusDays(1), LocalTime.of(10, 0));
        authenticate(fixture.usuario1);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaService.criar(new CriarConsultaRequest(fixture.slot.getId(), "teste")));

        assertEquals("Não é permitido agendar em data passada", ex.getMessage());
    }

    @Test
    void deveFalharAgendamentoFeriado() {
        Fixture fixture = fixtureBase(LocalDate.now().plusDays(2), LocalTime.of(10, 0));
        Feriado feriado = new Feriado();
        feriado.setData(fixture.slot.getData());
        feriado.setDescricao("Feriado Teste");
        feriado.setNacionalOuMunicipal("NACIONAL");
        feriadoRepository.save(feriado);

        authenticate(fixture.usuario1);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaService.criar(new CriarConsultaRequest(fixture.slot.getId(), "teste")));

        assertEquals("Não é permitido agendar em feriados", ex.getMessage());
    }

    @Test
    void deveFalharCancelamentoComMenosDe12h() {
        Fixture fixture = fixtureBase(LocalDate.now(), LocalTime.now().plusHours(6).withMinute(0).withSecond(0).withNano(0));
        authenticate(fixture.usuario1);
        ConsultaResponse consulta = consultaService.criar(new CriarConsultaRequest(fixture.slot.getId(), "teste"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaService.cancelarMinhaConsulta(consulta.id(), new CancelarConsultaRequest("imprevisto")));

        assertEquals("Cancelamento permitido somente com 12h de antecedência", ex.getMessage());
    }

    @Test
    void cancelamentoValidoDeveLiberarVaga() {
        Fixture fixture = fixtureBase(LocalDate.now().plusDays(2), LocalTime.of(15, 0));
        authenticate(fixture.usuario1);
        ConsultaResponse consulta = consultaService.criar(new CriarConsultaRequest(fixture.slot.getId(), "teste"));

        ConsultaResponse cancelada = consultaService.cancelarMinhaConsulta(consulta.id(), new CancelarConsultaRequest("não posso ir"));

        AgendaSlot slotAtualizado = agendaSlotRepository.findById(fixture.slot.getId()).orElseThrow();
        assertEquals(ConsultaStatus.CANCELADA_PELO_PACIENTE, cancelada.status());
        assertTrue(slotAtualizado.isDisponivel());
    }

    @Test
    void encaixeAutomaticoDeveOcuparVagaLiberada() {
        Fixture fixture = fixtureBase(LocalDate.now().plusDays(3), LocalTime.of(14, 0));

        FilaEspera fila = new FilaEspera();
        fila.setUsuario(fixture.usuario2);
        fila.setUbs(fixture.ubs);
        fila.setMedico(fixture.medico);
        fila.setDataDesejada(fixture.slot.getData());
        fila.setPrioridade(1);
        fila.setStatus(FilaEsperaStatus.ATIVA);
        filaEsperaRepository.save(fila);

        authenticate(fixture.usuario1);
        ConsultaResponse consulta = consultaService.criar(new CriarConsultaRequest(fixture.slot.getId(), "teste"));
        consultaService.cancelarMinhaConsulta(consulta.id(), new CancelarConsultaRequest("cancelar"));

        List<Consulta> consultasNoSlot = consultaRepository.findAll().stream()
                .filter(c -> c.getAgendaSlot().getId().equals(fixture.slot.getId()))
                .toList();

        assertEquals(2, consultasNoSlot.size());
        assertTrue(consultasNoSlot.stream().anyMatch(c -> c.getStatus() == ConsultaStatus.ENCAIXADA && c.isEncaixeAutomatico()));

        AgendaSlot slotAtualizado = agendaSlotRepository.findById(fixture.slot.getId()).orElseThrow();
        assertFalse(slotAtualizado.isDisponivel());
    }

    private Fixture fixtureBase(LocalDate data, LocalTime hora) {
        Ubs ubs = new Ubs();
        ubs.setNome("UBS Teste");
        ubs.setEndereco("Rua A");
        ubs.setCidade("Cidade");
        ubs.setEstado("SP");
        ubs.setTelefone("11999999999");
        ubs = ubsRepository.save(ubs);

        Medico medico = new Medico();
        medico.setNome("Dra. Teste");
        medico.setEspecialidade("Clínico");
        medico.setAtivo(true);
        medico.setUbs(ubs);
        medico = medicoRepository.save(medico);

        Usuario usuario1 = new Usuario();
        usuario1.setNome("Paciente 1");
        usuario1.setCpf("39053344705");
        usuario1.setCartaoSus("706105865820111");
        usuario1.setEmail("p1@test.com");
        usuario1.setTelefone("11999999999");
        usuario1.setSenhaHash(passwordEncoder.encode("123456"));
        usuario1.setRole(Role.CIDADAO);
        usuario1.setUbsReferencia(ubs);
        usuario1 = usuarioRepository.save(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Paciente 2");
        usuario2.setCpf("52998224725");
        usuario2.setCartaoSus("706105865820112");
        usuario2.setEmail("p2@test.com");
        usuario2.setTelefone("11999999998");
        usuario2.setSenhaHash(passwordEncoder.encode("123456"));
        usuario2.setRole(Role.CIDADAO);
        usuario2.setUbsReferencia(ubs);
        usuario2 = usuarioRepository.save(usuario2);

        AgendaSlot slot = new AgendaSlot();
        slot.setUbs(ubs);
        slot.setMedico(medico);
        slot.setData(data);
        slot.setHoraInicio(hora);
        slot.setHoraFim(hora.plusMinutes(50));
        slot.setDisponivel(true);
        slot = agendaSlotRepository.save(slot);

        return new Fixture(ubs, medico, usuario1, usuario2, slot);
    }

    private void authenticate(Usuario usuario) {
        UserPrincipal principal = new UserPrincipal(usuario);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private record Fixture(Ubs ubs, Medico medico, Usuario usuario1, Usuario usuario2, AgendaSlot slot) {
    }
}