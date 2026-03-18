package br.gov.hackgov.repository;

import br.gov.hackgov.domain.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdOrderByDataEnvioDesc(Long usuarioId);
    boolean existsByConsultaIdAndTipo(Long consultaId, br.gov.hackgov.domain.NotificacaoTipo tipo);
}

