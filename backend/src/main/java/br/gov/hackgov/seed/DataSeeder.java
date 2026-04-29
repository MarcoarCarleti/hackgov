package br.gov.hackgov.seed;

import br.gov.hackgov.domain.AgendaSlot;
import br.gov.hackgov.domain.Consulta;
import br.gov.hackgov.domain.ConsultaStatus;
import br.gov.hackgov.domain.Especialidade;
import br.gov.hackgov.domain.Feriado;
import br.gov.hackgov.domain.FilaEspera;
import br.gov.hackgov.domain.FilaEsperaStatus;
import br.gov.hackgov.domain.Medico;
import br.gov.hackgov.domain.Notificacao;
import br.gov.hackgov.domain.NotificacaoStatus;
import br.gov.hackgov.domain.NotificacaoTipo;
import br.gov.hackgov.domain.ProfissionalEspecialidade;
import br.gov.hackgov.domain.Role;
import br.gov.hackgov.domain.Ubs;
import br.gov.hackgov.domain.Usuario;
import br.gov.hackgov.repository.AgendaSlotRepository;
import br.gov.hackgov.repository.ConsultaRepository;
import br.gov.hackgov.repository.EspecialidadeRepository;
import br.gov.hackgov.repository.FeriadoRepository;
import br.gov.hackgov.repository.FilaEsperaRepository;
import br.gov.hackgov.repository.MedicoRepository;
import br.gov.hackgov.repository.NotificacaoRepository;
import br.gov.hackgov.repository.UbsRepository;
import br.gov.hackgov.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final UbsRepository ubsRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendaSlotRepository agendaSlotRepository;
    private final ConsultaRepository consultaRepository;
    private final FeriadoRepository feriadoRepository;
    private final FilaEsperaRepository filaEsperaRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UbsRepository ubsRepository,
                      MedicoRepository medicoRepository,
                      EspecialidadeRepository especialidadeRepository,
                      UsuarioRepository usuarioRepository,
                      AgendaSlotRepository agendaSlotRepository,
                      ConsultaRepository consultaRepository,
                      FeriadoRepository feriadoRepository,
                      FilaEsperaRepository filaEsperaRepository,
                      NotificacaoRepository notificacaoRepository,
                      PasswordEncoder passwordEncoder) {
        this.ubsRepository = ubsRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.agendaSlotRepository = agendaSlotRepository;
        this.consultaRepository = consultaRepository;
        this.feriadoRepository = feriadoRepository;
        this.filaEsperaRepository = filaEsperaRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (ubsRepository.count() > 0) {
            return;
        }

        Ubs ubsCentro = criarUbs("UBS Centro", "Rua das Flores, 100", "São Paulo", "SP", "1133334444");
        Ubs ubsNorte = criarUbs("UBS Zona Norte", "Av. Brasil, 2500", "São Paulo", "SP", "1144445555");

        Map<String, Especialidade> especialidades = criarEspecialidades();

        List<Medico> medicos = medicoRepository.saveAll(List.of(
                criarMedico("Ana Paula Souza", "CRM-SP-1001", ubsCentro, especialidades.get("Clínico Geral"), especialidades.get("Saúde da Família")),
                criarMedico("Ricardo Gomes", "CRM-SP-1002", ubsCentro, especialidades.get("Pediatria")),
                criarMedico("Marina Costa", "CRM-SP-1003", ubsCentro, especialidades.get("Ginecologia")),
                criarMedico("João Mendes", "CRM-SP-1004", ubsNorte, especialidades.get("Clínico Geral")),
                criarMedico("Carla Nunes", "CRM-SP-1005", ubsNorte, especialidades.get("Cardiologia")),
                criarMedico("Felipe Prado", "CRM-SP-1006", ubsNorte, especialidades.get("Dermatologia"))
        ));

        criarFeriados();

        Usuario admin = criarUsuario("Admin HackGov", "11144477735", "706105865820001", "admin@hackgov.local", "11990000001", "123456", Role.ADMIN, ubsCentro);
        Usuario gestor = criarUsuario("Gestor UBS", "52998224725", "706105865820002", "gestor@hackgov.local", "11990000002", "123456", Role.GESTOR, ubsCentro);
        Usuario pacienteDemo = criarUsuario("Paciente Demo", "39053344705", "706105865820003", "paciente@hackgov.local", "11990000003", "123456", Role.PACIENTE, ubsCentro);

        List<Usuario> pacientes = new ArrayList<>();
        pacientes.add(pacienteDemo);
        for (int i = 0; i < 79; i++) {
            pacientes.add(criarUsuario(
                    "Paciente " + (i + 1),
                    gerarCpfValido(i + 100),
                    "70610586582" + String.format("%04d", i + 4),
                    "paciente" + (i + 1) + "@hackgov.local",
                    "1198" + String.format("%07d", i + 1),
                    "123456",
                    Role.PACIENTE,
                    i % 2 == 0 ? ubsCentro : ubsNorte
            ));
        }

        usuarioRepository.save(admin);
        usuarioRepository.save(gestor);
        usuarioRepository.saveAll(pacientes);

        List<AgendaSlot> slots = criarSlots(medicos);
        agendaSlotRepository.saveAll(slots);

        criarConsultasDemo(slots, pacientes);
        criarFilaEsperaDemo(pacientes, medicos);
        criarNotificacoesIniciais();
    }

    private Map<String, Especialidade> criarEspecialidades() {
        Map<String, Especialidade> especialidades = new LinkedHashMap<>();
        especialidades.put("Clínico Geral", especialidade("Clínico Geral", "Atendimento clínico inicial e acompanhamento geral."));
        especialidades.put("Pediatria", especialidade("Pediatria", "Atendimento médico infantil."));
        especialidades.put("Ginecologia", especialidade("Ginecologia", "Saúde da mulher e acompanhamento ginecológico."));
        especialidades.put("Cardiologia", especialidade("Cardiologia", "Avaliação e acompanhamento cardiovascular."));
        especialidades.put("Dermatologia", especialidade("Dermatologia", "Cuidados com pele, unhas e cabelos."));
        especialidades.put("Saúde da Família", especialidade("Saúde da Família", "Acompanhamento longitudinal na atenção básica."));
        especialidadeRepository.saveAll(especialidades.values());
        return especialidades;
    }

    private Especialidade especialidade(String nome, String descricao) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(nome);
        especialidade.setDescricao(descricao);
        especialidade.setAtivo(true);
        return especialidade;
    }

    private Ubs criarUbs(String nome, String endereco, String cidade, String estado, String telefone) {
        Ubs ubs = new Ubs();
        ubs.setNome(nome);
        ubs.setEndereco(endereco);
        ubs.setCidade(cidade);
        ubs.setEstado(estado);
        ubs.setTelefone(telefone);
        return ubsRepository.save(ubs);
    }

    private Medico criarMedico(String nome, String registroConselho, Ubs ubs, Especialidade especialidadePrincipal, Especialidade... especialidadesSecundarias) {
        Medico medico = new Medico();
        medico.setNome(nome);
        medico.setRegistroConselho(registroConselho);
        medico.setUbs(ubs);
        medico.setAtivo(true);
        medico.adicionarEspecialidade(especialidadePrincipal, true);
        for (Especialidade especialidade : especialidadesSecundarias) {
            medico.adicionarEspecialidade(especialidade, false);
        }
        return medico;
    }

    private Usuario criarUsuario(String nome,
                                 String cpf,
                                 String cartaoSus,
                                 String email,
                                 String telefone,
                                 String senha,
                                 Role role,
                                 Ubs ubs) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setCartaoSus(cartaoSus);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setRole(role);
        usuario.setUbsReferencia(ubs);
        return usuario;
    }

    private void criarFeriados() {
        List<Feriado> feriados = new ArrayList<>();
        feriados.add(feriado(LocalDate.now().withDayOfMonth(1), "Confraternização", "NACIONAL"));
        feriados.add(feriado(LocalDate.now().plusDays(7), "Feriado Municipal", "MUNICIPAL"));
        feriados.add(feriado(LocalDate.now().minusDays(10), "Dia Estadual", "ESTADUAL"));
        feriadoRepository.saveAll(feriados);
    }

    private Feriado feriado(LocalDate data, String descricao, String tipo) {
        Feriado feriado = new Feriado();
        feriado.setData(data);
        feriado.setDescricao(descricao);
        feriado.setNacionalOuMunicipal(tipo);
        return feriado;
    }

    private List<AgendaSlot> criarSlots(List<Medico> medicos) {
        List<AgendaSlot> slots = new ArrayList<>();
        List<LocalTime> horarios = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
        );

        LocalDate inicio = LocalDate.now().minusDays(35);
        LocalDate fim = LocalDate.now().plusDays(20);
        for (LocalDate data = inicio; !data.isAfter(fim); data = data.plusDays(1)) {
            if (data.getDayOfWeek().getValue() >= 6 || feriadoRepository.existsByData(data)) {
                continue;
            }
            for (Medico medico : medicos) {
                Especialidade especialidadePrincipal = medico.getEspecialidades().stream()
                        .filter(ProfissionalEspecialidade::isPrincipal)
                        .map(ProfissionalEspecialidade::getEspecialidade)
                        .findFirst()
                        .orElseThrow();

                for (LocalTime hora : horarios) {
                    AgendaSlot slot = new AgendaSlot();
                    slot.setMedico(medico);
                    slot.setEspecialidade(especialidadePrincipal);
                    slot.setData(data);
                    slot.setHoraInicio(hora);
                    slot.setHoraFim(hora.plusMinutes(50));
                    slot.setDisponivel(true);
                    slots.add(slot);
                }
            }
        }
        return slots;
    }

    private void criarConsultasDemo(List<AgendaSlot> slots, List<Usuario> pacientes) {
        Random random = new Random(42);
        List<AgendaSlot> elegiveis = new ArrayList<>(slots);
        elegiveis.removeIf(s -> s.getData().isBefore(LocalDate.now().minusDays(30)) || s.getData().isAfter(LocalDate.now().plusDays(10)));
        Collections.shuffle(elegiveis, random);

        int total = Math.min(500, elegiveis.size());
        int faltas = (int) Math.round(total * 0.30);
        int cancelamentos = (int) Math.round(total * 0.10);
        int encaixes = 35;

        List<Consulta> consultas = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            AgendaSlot slot = elegiveis.get(i);
            Usuario usuario = escolherUsuarioCompativel(pacientes, slot.getUbs().getId(), random);

            Consulta consulta = new Consulta();
            consulta.setUsuario(usuario);
            consulta.setAgendaSlot(slot);
            consulta.setObservacoes("Consulta seed");

            if (i < faltas) {
                consulta.setStatus(ConsultaStatus.FALTA);
                slot.setDisponivel(false);
            } else if (i < faltas + cancelamentos) {
                consulta.setStatus(ConsultaStatus.CANCELADA_PELO_PACIENTE);
                consulta.setCanceladoEm(OffsetDateTime.now().minusDays(random.nextInt(20) + 1));
                consulta.setMotivoCancelamento("Cancelamento prévio");
                slot.setDisponivel(true);
            } else if (i < faltas + cancelamentos + encaixes) {
                consulta.setStatus(ConsultaStatus.ENCAIXADA);
                consulta.setEncaixeAutomatico(true);
                slot.setDisponivel(false);
            } else if (slot.getData().isBefore(LocalDate.now())) {
                consulta.setStatus(ConsultaStatus.REALIZADA);
                slot.setDisponivel(false);
            } else {
                consulta.setStatus(ConsultaStatus.AGENDADA);
                slot.setDisponivel(false);
            }

            consultas.add(consulta);
        }

        consultaRepository.saveAll(consultas);
        agendaSlotRepository.saveAll(slots);
    }

    private Usuario escolherUsuarioCompativel(List<Usuario> pacientes, Long ubsId, Random random) {
        List<Usuario> candidatos = pacientes.stream()
                .filter(u -> u.getUbsReferencia().getId().equals(ubsId))
                .toList();
        return candidatos.get(random.nextInt(candidatos.size()));
    }

    private void criarFilaEsperaDemo(List<Usuario> pacientes, List<Medico> medicos) {
        Random random = new Random(43);
        List<FilaEspera> filas = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Usuario usuario = pacientes.get(random.nextInt(pacientes.size()));
            FilaEspera fila = new FilaEspera();
            fila.setUsuario(usuario);
            fila.setUbs(usuario.getUbsReferencia());
            if (i % 3 == 0) {
                List<Medico> medicosUbs = medicos.stream()
                        .filter(m -> m.getUbs().getId().equals(usuario.getUbsReferencia().getId()))
                        .toList();
                fila.setMedico(medicosUbs.get(random.nextInt(medicosUbs.size())));
            }
            fila.setDataDesejada(LocalDate.now().plusDays(random.nextInt(14) + 1));
            fila.setPrioridade(50 + random.nextInt(70));
            fila.setStatus(FilaEsperaStatus.ATIVA);
            filas.add(fila);
        }
        filaEsperaRepository.saveAll(filas);
    }

    private void criarNotificacoesIniciais() {
        List<Consulta> futuras = consultaRepository.findFuturasAtivas(LocalDate.now(), LocalTime.now());
        List<Notificacao> notificacoes = new ArrayList<>();

        for (Consulta consulta : futuras.stream().limit(15).toList()) {
            Notificacao not = new Notificacao();
            not.setUsuario(consulta.getUsuario());
            not.setConsulta(consulta);
            not.setTipo(NotificacaoTipo.LEMBRETE_24H);
            not.setDataEnvio(OffsetDateTime.now().minusHours(1));
            not.setStatus(NotificacaoStatus.ENVIADA);
            not.setConteudo("Lembrete 24h (seed): consulta " + consulta.getDataConsulta() + " às " + consulta.getHoraConsulta());
            notificacoes.add(not);
        }

        notificacaoRepository.saveAll(notificacoes);
    }

    private String gerarCpfValido(int seed) {
        Random random = new Random(seed);
        int[] digits = new int[11];
        for (int i = 0; i < 9; i++) {
            digits[i] = random.nextInt(10);
        }

        digits[9] = calcularDigito(digits, 9, 10);
        digits[10] = calcularDigito(digits, 10, 11);

        StringBuilder cpf = new StringBuilder();
        for (int d : digits) {
            cpf.append(d);
        }
        return cpf.toString();
    }

    private int calcularDigito(int[] digits, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * (weightStart - i);
        }
        int mod = 11 - (sum % 11);
        return mod >= 10 ? 0 : mod;
    }
}
