package br.gov.hackgov.service;

import br.gov.hackgov.domain.Consulta;
import br.gov.hackgov.domain.ConsultaStatus;
import br.gov.hackgov.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Prova de conceito de IA para planejamento agregado da UBS.
 * As estimativas nao alteram agendamentos nem automatizam decisoes sobre pacientes.
 */
@Service
@Transactional(readOnly = true)
public class RiscoFaltasService {

    private final ConsultaRepository consultaRepository;

    public RiscoFaltasService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public record ProjecaoDia(String dia, long consultas, double riscoMedioPercentual,
                              double faltasEsperadas) {}
    public record ResultadoRisco(String status, int amostrasHistoricas,
                                int faltasHistoricas, int consultasFuturas,
                                List<ProjecaoDia> projecoes, String observacao) {}

    public ResultadoRisco analisar(Long ubsId) {
        LocalDate hoje = LocalDate.now();
        List<Consulta> historico = consultaRepository
                .findByDataConsultaBetween(hoje.minusDays(180), hoje.minusDays(1))
                .stream()
                .filter(c -> c.getStatus() == ConsultaStatus.FALTA || c.getStatus() == ConsultaStatus.REALIZADA)
                .filter(c -> ubsId == null || c.getUbs().getId().equals(ubsId))
                .toList();

        int faltas = (int) historico.stream()
                .filter(c -> c.getStatus() == ConsultaStatus.FALTA).count();

        if (historico.size() < 20 || faltas == 0 || faltas == historico.size()) {
            return new ResultadoRisco("DADOS_INSUFICIENTES", historico.size(), faltas, 0,
                    List.of(), "Sao necessarios pelo menos 20 resultados historicos e exemplos de ambas as classes.");
        }

        NaiveBayesAbsenteismo modelo = new NaiveBayesAbsenteismo(
                historico.stream().map(this::transformarEmAmostra).toList());

        List<Consulta> futuras = consultaRepository.findByDataConsultaBetween(hoje, hoje.plusDays(30))
                .stream()
                .filter(c -> c.getStatus() == ConsultaStatus.AGENDADA || c.getStatus() == ConsultaStatus.ENCAIXADA)
                .filter(c -> ubsId == null || c.getUbs().getId().equals(ubsId))
                .filter(c -> c.getDataConsulta().isAfter(hoje)
                        || c.getHoraConsulta().isAfter(LocalTime.now()))
                .toList();

        Map<LocalDate, Acumulador> porDia = new TreeMap<>();
        for (Consulta consulta : futuras) {
            double risco = modelo.probabilidadeFalta(transformarEmAmostra(consulta));
            porDia.computeIfAbsent(consulta.getDataConsulta(), data -> new Acumulador())
                    .adicionar(risco);
        }

        List<ProjecaoDia> projecoes = porDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(item -> new ProjecaoDia(item.getKey().toString(),
                        item.getValue().quantidade,
                        arredondar(100.0 * item.getValue().soma / item.getValue().quantidade),
                        arredondar(item.getValue().soma)))
                .toList();

        return new ResultadoRisco("OK", historico.size(), faltas, futuras.size(), projecoes,
                "Estimativa experimental com dados de demonstracao; nao validada clinicamente. " +
                "Nao usar para restringir acesso, penalizar pessoas ou efetuar encaixes automaticos.");
    }

    private NaiveBayesAbsenteismo.Amostra transformarEmAmostra(Consulta consulta) {
        return new NaiveBayesAbsenteismo.Amostra(
                consulta.getDataConsulta().getDayOfWeek().getValue(),
                consulta.getHoraConsulta().getHour() < 12 ? 0 : 1,
                consulta.getUbs().getId(),
                consulta.getStatus() == ConsultaStatus.FALTA);
    }

    private double arredondar(double numero) {
        return Math.round(numero * 100.0) / 100.0;
    }

    private static final class Acumulador {
        private int quantidade = 0;
        private double soma = 0.0;

        void adicionar(double risco) {
            quantidade++;
            soma += risco;
        }
    }
}
