package br.gov.hackgov.repository;

import br.gov.hackgov.domain.FilaEspera;
import br.gov.hackgov.domain.FilaEsperaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FilaEsperaRepository extends JpaRepository<FilaEspera, Long> {

    List<FilaEspera> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    List<FilaEspera> findByUbsIdAndStatusAndDataDesejadaLessThanEqualAndMedicoIdOrderByPrioridadeAscCreatedAtAsc(
            Long ubsId,
            FilaEsperaStatus status,
            LocalDate data,
            Long medicoId
    );

    List<FilaEspera> findByUbsIdAndStatusAndDataDesejadaLessThanEqualAndMedicoIsNullOrderByPrioridadeAscCreatedAtAsc(
            Long ubsId,
            FilaEsperaStatus status,
            LocalDate data
    );
}

