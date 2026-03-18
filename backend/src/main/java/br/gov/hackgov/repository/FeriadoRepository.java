package br.gov.hackgov.repository;

import br.gov.hackgov.domain.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    boolean existsByData(LocalDate data);
}
