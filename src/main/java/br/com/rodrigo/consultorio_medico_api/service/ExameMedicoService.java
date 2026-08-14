package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.mapper.ExameMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import br.com.rodrigo.consultorio_medico_api.repository.ExameMedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.HistoricoMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExameMedicoService {

    private final ExameMedicoRepository exameMedicoRepository;
    private final HistoricoMedicoRepository historicoMedicoRepository;
    private final ExameMedicoMapper exameMedicoMapper;

    @Autowired
    public ExameMedicoService(ExameMedicoRepository exameMedicoRepository, HistoricoMedicoRepository historicoMedicoRepository, ExameMedicoMapper exameMedicoMapper) {
        this.exameMedicoRepository = exameMedicoRepository;
        this.historicoMedicoRepository = historicoMedicoRepository;
        this.exameMedicoMapper = exameMedicoMapper;
    }

    // Adicionar um exame médico
    public ExameMedicoResponse adicionarExame(UUID historicoMedicoId, ExameMedicoRequest exameInput) {
        HistoricoMedico historicoMedico = historicoMedicoRepository.findById(historicoMedicoId)
                .orElseThrow(() -> new RuntimeException("Histórico médico não encontrado"));

        ExameMedico exameMedico = new ExameMedico();
        exameMedico.setHistoricoMedico(historicoMedico);
        exameMedico.setTipoExame(exameInput.tipoExame());
        exameMedico.setResultado(exameInput.resultado());
        exameMedico.setDataExame(exameInput.dataExame());
        exameMedico.setArquivoUrl(exameInput.arquivoUrl());

        // Salvar exame médico
        return exameMedicoMapper.toOutput(exameMedicoRepository.save(exameMedico));
    }
}

