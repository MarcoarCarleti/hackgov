package br.gov.hackgov.repository;

import br.gov.hackgov.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByUbsIdAndAtivoTrue(Long ubsId);
}
