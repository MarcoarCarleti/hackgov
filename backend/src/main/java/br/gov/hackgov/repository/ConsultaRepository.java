package br.gov.hackgov.repository;

import br.gov.hackgov.domain.Consulta;
import br.gov.hackgov.domain.ConsultaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
            select c from Consulta c
            where c.usuario.id = :usuarioId
            order by c.agendaSlot.data desc, c.agendaSlot.horaInicio desc
            """)
    List<Consulta> findByUsuarioIdOrderByDataConsultaDescHoraConsultaDesc(Long usuarioId);

    @Query("""
            select c from Consulta c
            order by c.agendaSlot.data desc, c.agendaSlot.horaInicio desc
            """)
    List<Consulta> findAllByOrderByDataConsultaDescHoraConsultaDesc();

    @Query("""
            select c from Consulta c
            where c.agendaSlot.medico.ubs.id = :ubsId
            order by c.agendaSlot.data desc, c.agendaSlot.horaInicio desc
            """)
    List<Consulta> findByUbsIdOrderByDataConsultaDescHoraConsultaDesc(Long ubsId);

    @Query("""
            select c from Consulta c
            where c.agendaSlot.data between :inicio and :fim
            """)
    List<Consulta> findByDataConsultaBetween(LocalDate inicio, LocalDate fim);

    boolean existsByUsuarioIdAndAgendaSlotDataAndAgendaSlotHoraInicioAndStatusIn(Long usuarioId,
                                                                                  LocalDate dataConsulta,
                                                                                  LocalTime horaConsulta,
                                                                                  List<ConsultaStatus> status);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and c.agendaSlot.data = :data
            and c.agendaSlot.horaInicio = :hora
            """)
    List<Consulta> findAtivasByDataHora(LocalDate data, LocalTime hora);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and (c.agendaSlot.data > :today or (c.agendaSlot.data = :today and c.agendaSlot.horaInicio >= :hour))
            """)
    List<Consulta> findFuturasAtivas(LocalDate today, LocalTime hour);

    @Query("""
            select c from Consulta c
            where c.status in ('AGENDADA','ENCAIXADA')
            and c.agendaSlot.data = :data
            and c.agendaSlot.horaInicio = :hora
            and c.id <> :excludeId
            and c.usuario.id = :usuarioId
            """)
    List<Consulta> findConflitos(Long usuarioId, LocalDate data, LocalTime hora, Long excludeId);

    long countByAgendaSlotDataBetween(LocalDate inicio, LocalDate fim);
    long countByAgendaSlotDataBetweenAndStatus(LocalDate inicio, LocalDate fim, ConsultaStatus status);
    long countByAgendaSlotDataBetweenAndStatusIn(LocalDate inicio, LocalDate fim, List<ConsultaStatus> status);

    @Query("""
            select c.agendaSlot.medico.nome, count(c.id)
            from Consulta c
            where c.agendaSlot.data between :inicio and :fim
            and c.status in ('REALIZADA','AGENDADA','ENCAIXADA','FALTA')
            group by c.agendaSlot.medico.nome
            order by count(c.id) desc
            """)
    List<Object[]> rankingPorMedico(LocalDate inicio, LocalDate fim);

    @Query("""
            select c.agendaSlot.data, count(c.id),
            sum(case when c.status = 'FALTA' then 1 else 0 end)
            from Consulta c
            where c.agendaSlot.data between :inicio and :fim
            group by c.agendaSlot.data
            order by c.agendaSlot.data
            """)
    List<Object[]> serieAgendamentosEFaltas(LocalDate inicio, LocalDate fim);

    @Query("""
            select c.agendaSlot.data, count(c.id)
            from Consulta c
            where c.agendaSlot.data between :inicio and :fim
            and c.status in ('AGENDADA','ENCAIXADA','REALIZADA','FALTA')
            group by c.agendaSlot.data
            order by c.agendaSlot.data
            """)
    List<Object[]> ocupacaoPorDia(LocalDate inicio, LocalDate fim);
}

