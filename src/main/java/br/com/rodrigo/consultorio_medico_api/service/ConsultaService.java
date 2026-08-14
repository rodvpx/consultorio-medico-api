package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.mapper.ConsultaMapper;
import br.com.rodrigo.consultorio_medico_api.repository.ConsultaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final RecepcionistaRepository recepcionistaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaMapper consultaMapper;

    @Autowired
    public ConsultaService(ConsultaRepository consultaRepository, RecepcionistaRepository recepcionistaRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository, ConsultaMapper consultaMapper) {
        this.consultaRepository = consultaRepository;
        this.recepcionistaRepository = recepcionistaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaMapper = consultaMapper;
    }


    // Agendar uma consulta
    public ConsultaResponse agendarConsulta(ConsultaRequest consultaInput) {
        // Verifica se o paciente, médico e recepcionista existem
        Paciente paciente = pacienteRepository.findById(consultaInput.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(consultaInput.medicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        Recepcionista recepcionista = recepcionistaRepository.findById(consultaInput.recepcionistaId())
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado"));

        // Criação da consulta
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setRecepcionista(recepcionista);
        consulta.setDataHora(consultaInput.dataHora());
        consulta.setStatus(Consulta.StatusConsulta.AGENDADA);
        consulta.setMotivoCancelamento(null);
        consulta.setObservacoes(consultaInput.observacoes());

        return consultaMapper.toOutput(consultaRepository.save(consulta));
    }

    // Atualizar status da consulta
    public ConsultaResponse atualizarStatus(UUID id, Consulta.StatusConsulta status) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.setStatus(status);
        return consultaMapper.toOutput(consultaRepository.save(consulta));
    }

    // Buscar consultas de um paciente
    public List<ConsultaResponse> buscarConsultasPorPaciente(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        List<Consulta> consultas = consultaRepository.findByPaciente(paciente);
        return consultaMapper.toOutputList(consultas);
    }
}
