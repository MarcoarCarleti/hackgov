package br.gov.hackgov.repository;

import br.gov.hackgov.domain.LogEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEventoRepository extends JpaRepository<LogEvento, Long> {
}
