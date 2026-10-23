package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoDetalhadoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.ExameMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.mapper.HistoricoMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.repository.ConsultaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.ExameMedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.HistoricoMedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoMedicoServiceTest {

    @Mock
    private HistoricoMedicoRepository historicoMedicoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private ExameMedicoRepository exameMedicoRepository;

    @Mock
    private HistoricoMedicoMapper historicoMedicoMapper;

    @Mock
    private ExameMedicoMapper exameMedicoMapper;

    @InjectMocks
    private HistoricoMedicoService service;

    @Test
    void deveCriarHistoricoMedico() {
        // given
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        HistoricoMedicoRequest input = new HistoricoMedicoRequest("Hipertensão", "Losartana", "Acompanhamento");
        HistoricoMedico historico = new HistoricoMedico();
        HistoricoMedicoResponse response = new HistoricoMedicoResponse(UUID.randomUUID(), "Paciente", "Médico",
                "REALIZADA", "Hipertensão", "Losartana", "Acompanhamento", LocalDateTime.now());

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(new Paciente()));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(new Medico()));
        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(new Consulta()));
        when(historicoMedicoRepository.save(any(HistoricoMedico.class))).thenReturn(historico);
        when(historicoMedicoMapper.toOutput(historico)).thenReturn(response);

        // when
        HistoricoMedicoResponse resultado = service.criarHistorico(pacienteId, medicoId, consultaId, input);

        // then
        assertEquals(response, resultado);
    }

    @Test
    void deveLancarNaoEncontradoQuandoPacienteNaoExisteAoCriarHistorico() {
        // given
        UUID pacienteId = UUID.randomUUID();
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criarHistorico(pacienteId, UUID.randomUUID(), UUID.randomUUID(),
                        new HistoricoMedicoRequest("Diagnóstico", null, null)));
        verify(historicoMedicoRepository, never()).save(any(HistoricoMedico.class));
    }

    @Test
    void deveBuscarHistoricosDoPacienteComExames() {
        // given
        UUID pacienteId = UUID.randomUUID();
        Paciente paciente = new Paciente();
        HistoricoMedico historico = new HistoricoMedico();
        ExameMedicoResponse exame = new ExameMedicoResponse(UUID.randomUUID(), "Hemograma", "Normal",
                LocalDateTime.now(), null);
        HistoricoMedicoDetalhadoResponse response = new HistoricoMedicoDetalhadoResponse(UUID.randomUUID(),
                "Paciente", "Médico", "REALIZADA", "Hipertensão", "Losartana", null, LocalDateTime.now(),
                List.of(exame));

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(historicoMedicoRepository.findByPaciente(paciente)).thenReturn(List.of(historico));
        when(historicoMedicoMapper.toDetalhadoOutputList(List.of(historico))).thenReturn(List.of(response));

        // when
        List<HistoricoMedicoDetalhadoResponse> resultado = service.buscarPorPaciente(pacienteId);

        // then
        assertEquals(1, resultado.size());
        assertEquals(response, resultado.getFirst());
        assertEquals(1, resultado.getFirst().exames().size());
        verify(historicoMedicoRepository).findByPaciente(paciente);
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarHistoricoDePacienteInexistente() {
        // given
        UUID pacienteId = UUID.randomUUID();
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorPaciente(pacienteId));
        verify(historicoMedicoRepository, never()).findByPaciente(any(Paciente.class));
    }

    @Test
    void deveAdicionarExameAoHistorico() {
        // given
        UUID historicoId = UUID.randomUUID();
        ExameMedicoRequest input = new ExameMedicoRequest("Eletrocardiograma", "Normal",
                LocalDateTime.now(), "https://exemplo.com/exame.pdf");
        ExameMedico exame = new ExameMedico();
        ExameMedicoResponse response = new ExameMedicoResponse(UUID.randomUUID(), "Eletrocardiograma", "Normal",
                input.dataExame(), input.arquivoUrl());

        when(historicoMedicoRepository.findById(historicoId)).thenReturn(Optional.of(new HistoricoMedico()));
        when(exameMedicoRepository.save(any(ExameMedico.class))).thenReturn(exame);
        when(exameMedicoMapper.toOutput(exame)).thenReturn(response);

        // when
        ExameMedicoResponse resultado = service.adicionarExame(historicoId, input);

        // then
        assertEquals(response, resultado);
    }

    @Test
    void deveLancarNaoEncontradoQuandoHistoricoNaoExisteAoAdicionarExame() {
        // given
        UUID historicoId = UUID.randomUUID();
        when(historicoMedicoRepository.findById(historicoId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.adicionarExame(historicoId,
                        new ExameMedicoRequest("Exame", "Resultado", LocalDateTime.now(), null)));
        verify(exameMedicoRepository, never()).save(any(ExameMedico.class));
    }
}
