package br.gov.hackgov.repository;

import br.gov.hackgov.domain.AgendaSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaSlotRepository extends JpaRepository<AgendaSlot, Long> {
    List<AgendaSlot> findByMedicoUbsIdAndDataAndDisponivelTrueOrderByHoraInicioAsc(Long ubsId, LocalDate data);
    List<AgendaSlot> findByMedicoUbsIdAndDataBetweenAndDisponivelTrueOrderByDataAscHoraInicioAsc(Long ubsId, LocalDate inicio, LocalDate fim);
    List<AgendaSlot> findByDisponivelTrueAndDataGreaterThanEqualOrderByDataAscHoraInicioAsc(LocalDate data);
}

