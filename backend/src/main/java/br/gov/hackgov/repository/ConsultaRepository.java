package br.gov.hackgov.repository;

import br.gov.hackgov.domain.Consulta;
import br.gov.hackgov.domain.ConsultaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByUsuarioIdOrderByDataConsultaDescHoraConsultaDesc(Long usuarioId);
    List<Consulta> findAllByOrderByDataConsultaDescHoraConsultaDesc();
    List<Consulta> findByUbsIdOrderByDataConsultaDescHoraConsultaDesc(Long ubsId);
    List<Consulta> findByDataConsultaBetween(LocalDate inicio, LocalDate fim);

    boolean existsByUsuarioIdAndDataConsultaAndHoraConsultaAndStatusIn(Long usuarioId,
                                                                       LocalDate dataConsulta,
                                                                       java.time.LocalTime horaConsulta,
                                                                       List<ConsultaStatus> status);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and c.dataConsulta = :data
            and c.horaConsulta = :hora
            """)
    List<Consulta> findAtivasByDataHora(LocalDate data, java.time.LocalTime hora);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and (c.dataConsulta > :today or (c.dataConsulta = :today and c.horaConsulta >= :hour))
            """)
    List<Consulta> findFuturasAtivas(LocalDate today, java.time.LocalTime hour);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and c.dataConsulta = :data
            and c.horaConsulta = :hora
            and c.id <> :excludeId
            and c.usuario.id = :usuarioId
            """)
    List<Consulta> findConflitos(Long usuarioId, LocalDate data, java.time.LocalTime hora, Long excludeId);

    long countByDataConsultaBetween(LocalDate inicio, LocalDate fim);
    long countByDataConsultaBetweenAndStatus(LocalDate inicio, LocalDate fim, ConsultaStatus status);
    long countByDataConsultaBetweenAndStatusIn(LocalDate inicio, LocalDate fim, List<ConsultaStatus> status);

    @Query("""
            select c.medico.nome, count(c.id)
            from Consulta c
            where c.dataConsulta between :inicio and :fim
            and c.status in ('REALIZADA','AGENDADA','ENCAIXADA','FALTA')
            group by c.medico.nome
            order by count(c.id) desc
            """)
    List<Object[]> rankingPorMedico(LocalDate inicio, LocalDate fim);

    @Query("""
            select c.dataConsulta, count(c.id),
            sum(case when c.status = 'FALTA' then 1 else 0 end)
            from Consulta c
            where c.dataConsulta between :inicio and :fim
            group by c.dataConsulta
            order by c.dataConsulta
            """)
    List<Object[]> serieAgendamentosEFaltas(LocalDate inicio, LocalDate fim);

    @Query("""
            select c.dataConsulta, count(c.id)
            from Consulta c
            where c.dataConsulta between :inicio and :fim
            and c.status in ('AGENDADA','ENCAIXADA','REALIZADA','FALTA')
            group by c.dataConsulta
            order by c.dataConsulta
            """)
    List<Object[]> ocupacaoPorDia(LocalDate inicio, LocalDate fim);
}

