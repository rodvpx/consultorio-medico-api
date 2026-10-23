package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.exception.RegraDeNegocioException;
import br.com.rodrigo.consultorio_medico_api.mapper.ConsultaMapper;
import br.com.rodrigo.consultorio_medico_api.repository.ConsultaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private ConsultaMapper consultaMapper;

    @InjectMocks
    private ConsultaService service;

    @Test
    void deveAgendarConsultaComStatusAgendada() {
        // given
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID recepcionistaId = UUID.randomUUID();
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        ConsultaRequest input = new ConsultaRequest(pacienteId, medicoId, recepcionistaId, dataHora, "Primeira consulta");

        Paciente paciente = new Paciente();
        Medico medico = new Medico();
        Recepcionista recepcionista = new Recepcionista();
        Consulta consultaSalva = new Consulta();
        ConsultaResponse response = new ConsultaResponse(UUID.randomUUID(), "Paciente", "Médico", "Recepcionista",
                dataHora, "AGENDADA", null, "Primeira consulta");

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(recepcionistaRepository.findById(recepcionistaId)).thenReturn(Optional.of(recepcionista));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaSalva);
        when(consultaMapper.toOutput(consultaSalva)).thenReturn(response);

        // when
        ConsultaResponse resultado = service.agendarConsulta(input);

        // then
        assertEquals(response, resultado);
        ArgumentCaptor<Consulta> captor = ArgumentCaptor.forClass(Consulta.class);
        verify(consultaRepository).save(captor.capture());
        assertEquals(Consulta.StatusConsulta.AGENDADA, captor.getValue().getStatus());
        assertEquals(dataHora, captor.getValue().getDataHora());
    }

    @Test
    void deveLancarNaoEncontradoQuandoPacienteNaoExisteAoAgendar() {
        // given
        UUID pacienteId = UUID.randomUUID();
        ConsultaRequest input = new ConsultaRequest(pacienteId, UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1), null);
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.agendarConsulta(input));
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void deveAtualizarStatusDaConsulta() {
        // given
        UUID id = UUID.randomUUID();
        Consulta consulta = new Consulta();
        consulta.setStatus(Consulta.StatusConsulta.AGENDADA);
        ConsultaResponse response = new ConsultaResponse(id, "Paciente", "Médico", "Recepcionista",
                LocalDateTime.now().plusDays(1), "CANCELADA", null, null);

        when(consultaRepository.findById(id)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);
        when(consultaMapper.toOutput(consulta)).thenReturn(response);

        // when
        ConsultaResponse resultado = service.atualizarStatus(id, Consulta.StatusConsulta.CANCELADA);

        // then
        assertEquals(response, resultado);
        assertEquals(Consulta.StatusConsulta.CANCELADA, consulta.getStatus());
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarStatusDeConsultaInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(consultaRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizarStatus(id, Consulta.StatusConsulta.REALIZADA));
    }

    @Test
    void deveLancarRegraDeNegocioQuandoStatusForNulo() {
        // when / then
        assertThrows(RegraDeNegocioException.class,
                () -> service.atualizarStatus(UUID.randomUUID(), null));
        verify(consultaRepository, never()).findById(any());
    }

    @Test
    void deveBuscarConsultasPorPaciente() {
        // given
        UUID pacienteId = UUID.randomUUID();
        Paciente paciente = new Paciente();
        Consulta consulta = new Consulta();
        ConsultaResponse response = new ConsultaResponse(UUID.randomUUID(), "Paciente", "Médico", "Recepcionista",
                LocalDateTime.now().plusDays(1), "AGENDADA", null, null);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(consultaRepository.findByPaciente(paciente)).thenReturn(List.of(consulta));
        when(consultaMapper.toOutputList(List.of(consulta))).thenReturn(List.of(response));

        // when
        List<ConsultaResponse> resultado = service.buscarConsultasPorPaciente(pacienteId);

        // then
        assertEquals(1, resultado.size());
        assertEquals(response, resultado.getFirst());
    }

    @Test
    void deveLancarNaoEncontradoQuandoPacienteNaoExisteNaBusca() {
        // given
        UUID pacienteId = UUID.randomUUID();
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarConsultasPorPaciente(pacienteId));
    }
}
