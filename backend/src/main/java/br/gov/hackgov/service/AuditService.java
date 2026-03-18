package br.gov.hackgov.service;

import br.gov.hackgov.domain.LogEvento;
import br.gov.hackgov.repository.LogEventoRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final LogEventoRepository logEventoRepository;

    public AuditService(LogEventoRepository logEventoRepository) {
        this.logEventoRepository = logEventoRepository;
    }

    public void registrar(String entidade, Long entidadeId, String acao, Long usuarioResponsavelId, String descricao) {
        LogEvento evento = new LogEvento();
        evento.setEntidade(entidade);
        evento.setEntidadeId(entidadeId);
        evento.setAcao(acao);
        evento.setUsuarioResponsavelId(usuarioResponsavelId);
        evento.setDescricao(descricao);
        logEventoRepository.save(evento);
    }
}
