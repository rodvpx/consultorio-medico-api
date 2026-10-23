package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoDetalhadoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.ExameMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.mapper.HistoricoMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.entity.*;
import br.com.rodrigo.consultorio_medico_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HistoricoMedicoService {

    private final HistoricoMedicoRepository historicoMedicoRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;
    private final ExameMedicoRepository exameMedicoRepository;
    private final HistoricoMedicoMapper historicoMedicoMapper;
    private final ExameMedicoMapper exameMedicoMapper;

    @Autowired
    public HistoricoMedicoService(HistoricoMedicoRepository historicoMedicoRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository, ConsultaRepository consultaRepository, ExameMedicoRepository exameMedicoRepository, HistoricoMedicoMapper historicoMedicoMapper, ExameMedicoMapper exameMedicoMapper) {
        this.historicoMedicoRepository = historicoMedicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
        this.exameMedicoRepository = exameMedicoRepository;
        this.historicoMedicoMapper = historicoMedicoMapper;
        this.exameMedicoMapper = exameMedicoMapper;
    }

    // Criar histórico médico
    public HistoricoMedicoResponse criarHistorico(UUID pacienteId, UUID medicoId, UUID consultaId, HistoricoMedicoRequest input) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado."));
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));

        HistoricoMedico historicoMedico = new HistoricoMedico();
        historicoMedico.setPaciente(paciente);
        historicoMedico.setMedico(medico);
        historicoMedico.setConsulta(consulta);
        historicoMedico.setDiagnostico(input.diagnostico());
        historicoMedico.setPrescricao(input.prescricao());
        historicoMedico.setObservacoes(input.observacoes());
        historicoMedico.setDataRegistro(LocalDateTime.now());

        return historicoMedicoMapper.toOutput(historicoMedicoRepository.save(historicoMedico));
    }

    // Buscar históricos de um paciente, com os exames vinculados
    @Transactional(readOnly = true)
    public List<HistoricoMedicoDetalhadoResponse> buscarPorPaciente(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        return historicoMedicoMapper.toDetalhadoOutputList(historicoMedicoRepository.findByPaciente(paciente));
    }

    // Adicionar exame médico ao histórico
    public ExameMedicoResponse adicionarExame(UUID historicoMedicoId, ExameMedicoRequest exameInput) {
        HistoricoMedico historicoMedico = historicoMedicoRepository.findById(historicoMedicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Histórico médico não encontrado."));

        ExameMedico exameMedico = new ExameMedico();
        exameMedico.setHistoricoMedico(historicoMedico);
        exameMedico.setTipoExame(exameInput.tipoExame());
        exameMedico.setResultado(exameInput.resultado());
        exameMedico.setDataExame(exameInput.dataExame());
        exameMedico.setArquivoUrl(exameInput.arquivoUrl());

        return exameMedicoMapper.toOutput(exameMedicoRepository.save(exameMedico));
    }
}
