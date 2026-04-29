package br.gov.hackgov.service;

import br.gov.hackgov.repository.MedicoRepository;
import br.gov.hackgov.repository.UbsRepository;
import br.gov.hackgov.web.dto.MedicoResponse;
import br.gov.hackgov.web.dto.UbsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final UbsRepository ubsRepository;
    private final MedicoRepository medicoRepository;

    public CatalogoService(UbsRepository ubsRepository, MedicoRepository medicoRepository) {
        this.ubsRepository = ubsRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<UbsResponse> listarUbs() {
        return ubsRepository.findAll().stream()
                .map(ubs -> new UbsResponse(
                        ubs.getId(),
                        ubs.getNome(),
                        ubs.getEndereco(),
                        ubs.getCidade(),
                        ubs.getEstado(),
                        ubs.getTelefone()
                ))
                .toList();
    }

    public List<MedicoResponse> listarMedicos() {
        return medicoRepository.findAll().stream()
                .map(medico -> new MedicoResponse(
                        medico.getId(),
                        medico.getNome(),
                        medico.getEspecialidadesDescricao(),
                        medico.getUbs().getId(),
                        medico.getUbs().getNome(),
                        medico.isAtivo()
                ))
                .toList();
    }
}
