package br.gov.hackgov.service;

import br.gov.hackgov.domain.Consulta;
import br.gov.hackgov.domain.ConsultaStatus;
import br.gov.hackgov.repository.ConsultaRepository;
import br.gov.hackgov.web.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ConsultaRepository consultaRepository;

    public DashboardService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public DashboardResumoResponse resumo(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        List<Consulta> consultas = consultasFiltradas(inicio, fim, ubsId, medicoId);
        long total = consultas.size();
        long realizadas = consultas.stream().filter(c -> c.getStatus() == ConsultaStatus.REALIZADA).count();
        long faltas = consultas.stream().filter(c -> c.getStatus() == ConsultaStatus.FALTA).count();
        long cancelamentos = consultas.stream().filter(c -> c.getStatus() == ConsultaStatus.CANCELADA_PELO_PACIENTE || c.getStatus() == ConsultaStatus.CANCELADA_PELO_SISTEMA).count();
        long reaproveitadas = consultas.stream().filter(Consulta::isEncaixeAutomatico).count();

        double taxaFaltas = total == 0 ? 0.0 : (faltas * 100.0) / total;
        double taxaCancelamentos = total == 0 ? 0.0 : (cancelamentos * 100.0) / total;

        return new DashboardResumoResponse(total, realizadas, faltas, cancelamentos, round(taxaFaltas), round(taxaCancelamentos), reaproveitadas);
    }

    public List<IndicadorDiaResponse> agendamentosPorDia(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        return groupByDia(consultasFiltradas(inicio, fim, ubsId, medicoId), c -> true);
    }

    public List<IndicadorDiaResponse> faltasPorDia(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        return groupByDia(consultasFiltradas(inicio, fim, ubsId, medicoId), c -> c.getStatus() == ConsultaStatus.FALTA);
    }

    public List<IndicadorDiaResponse> cancelamentosPorDia(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        return groupByDia(consultasFiltradas(inicio, fim, ubsId, medicoId),
                c -> c.getStatus() == ConsultaStatus.CANCELADA_PELO_PACIENTE || c.getStatus() == ConsultaStatus.CANCELADA_PELO_SISTEMA);
    }

    public List<ReaproveitamentoResponse> reaproveitamentoPorDia(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        Map<LocalDate, Long> porDia = consultasFiltradas(inicio, fim, ubsId, medicoId).stream()
                .filter(Consulta::isEncaixeAutomatico)
                .collect(Collectors.groupingBy(Consulta::getDataConsulta, Collectors.counting()));

        return porDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ReaproveitamentoResponse(entry.getKey().toString(), entry.getValue()))
                .toList();
    }

    public List<RankingMedicoResponse> rankingMedicos(LocalDate inicio, LocalDate fim, Long ubsId) {
        return consultasFiltradas(inicio, fim, ubsId, null).stream()
                .collect(Collectors.groupingBy(c -> c.getMedico().getNome(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new RankingMedicoResponse(e.getKey(), e.getValue()))
                .toList();
    }

    public List<SerieAgendamentoFaltaResponse> serieAgendamentosEFaltas(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        Map<LocalDate, List<Consulta>> porDia = consultasFiltradas(inicio, fim, ubsId, medicoId).stream()
                .collect(Collectors.groupingBy(Consulta::getDataConsulta));

        return porDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new SerieAgendamentoFaltaResponse(
                        e.getKey().toString(),
                        e.getValue().size(),
                        e.getValue().stream().filter(c -> c.getStatus() == ConsultaStatus.FALTA).count()
                ))
                .toList();
    }

    private List<Consulta> consultasFiltradas(LocalDate inicio, LocalDate fim, Long ubsId, Long medicoId) {
        LocalDate ini = inicio == null ? LocalDate.now().minusDays(30) : inicio;
        LocalDate end = fim == null ? LocalDate.now() : fim;

        return consultaRepository.findByDataConsultaBetween(ini, end)
                .stream()
                .filter(c -> ubsId == null || c.getUbs().getId().equals(ubsId))
                .filter(c -> medicoId == null || c.getMedico().getId().equals(medicoId))
                .toList();
    }

    private List<IndicadorDiaResponse> groupByDia(List<Consulta> consultas,
                                                  Function<Consulta, Boolean> predicate) {
        return consultas.stream()
                .filter(c -> predicate.apply(c))
                .collect(Collectors.groupingBy(Consulta::getDataConsulta, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new IndicadorDiaResponse(e.getKey().toString(), e.getValue()))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
