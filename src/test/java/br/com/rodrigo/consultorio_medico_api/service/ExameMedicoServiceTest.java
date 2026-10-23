package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.ExameMedicoMapper;
import br.com.rodrigo.consultorio_medico_api.repository.ExameMedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.HistoricoMedicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExameMedicoServiceTest {

    @Mock
    private ExameMedicoRepository exameMedicoRepository;

    @Mock
    private HistoricoMedicoRepository historicoMedicoRepository;

    @Mock
    private ExameMedicoMapper exameMedicoMapper;

    @InjectMocks
    private ExameMedicoService service;

    @Test
    void deveAdicionarExameAoHistorico() {
        // given
        UUID historicoId = UUID.randomUUID();
        ExameMedicoRequest input = new ExameMedicoRequest("Hemograma", "Normal",
                LocalDateTime.now(), "https://exemplo.com/hemograma.pdf");
        ExameMedico exameSalvo = new ExameMedico();
        ExameMedicoResponse response = new ExameMedicoResponse(UUID.randomUUID(), "Hemograma", "Normal",
                input.dataExame(), input.arquivoUrl());

        HistoricoMedico historico = new HistoricoMedico();
        when(historicoMedicoRepository.findById(historicoId)).thenReturn(Optional.of(historico));
        when(exameMedicoRepository.save(any(ExameMedico.class))).thenReturn(exameSalvo);
        when(exameMedicoMapper.toOutput(exameSalvo)).thenReturn(response);

        // when
        ExameMedicoResponse resultado = service.adicionarExame(historicoId, input);

        // then
        assertEquals(response, resultado);
        ArgumentCaptor<ExameMedico> captor = ArgumentCaptor.forClass(ExameMedico.class);
        verify(exameMedicoRepository).save(captor.capture());
        ExameMedico persistido = captor.getValue();
        assertEquals(historico, persistido.getHistoricoMedico());
        assertEquals("Hemograma", persistido.getTipoExame());
        assertEquals("Normal", persistido.getResultado());
        assertEquals(input.dataExame(), persistido.getDataExame());
        assertEquals("https://exemplo.com/hemograma.pdf", persistido.getArquivoUrl());
    }

    @Test
    void deveLancarNaoEncontradoQuandoHistoricoNaoExiste() {
        // given
        UUID historicoId = UUID.randomUUID();
        when(historicoMedicoRepository.findById(historicoId)).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.adicionarExame(historicoId,
                        new ExameMedicoRequest("Hemograma", "Normal", LocalDateTime.now(), null)));
        assertEquals("Histórico médico não encontrado.", ex.getMessage());
        verify(exameMedicoRepository, never()).save(any(ExameMedico.class));
    }
}
