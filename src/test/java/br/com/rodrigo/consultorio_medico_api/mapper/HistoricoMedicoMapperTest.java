package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoDetalhadoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HistoricoMedicoMapperTest {

    private HistoricoMedicoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(HistoricoMedicoMapper.class);
        ReflectionTestUtils.setField(mapper, "exameMedicoMapper", Mappers.getMapper(ExameMedicoMapper.class));
    }

    @Test
    void deveMapearRequestParaEntidade() {
        // given
        HistoricoMedicoRequest request = new HistoricoMedicoRequest("Hipertensão", "Losartana", "Acompanhar");

        // when
        HistoricoMedico entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getDiagnostico()).isEqualTo("Hipertensão");
        assertThat(entidade.getPrescricao()).isEqualTo("Losartana");
        assertThat(entidade.getObservacoes()).isEqualTo("Acompanhar");
    }

    @Test
    void deveMapearEntidadeComRelacionamentosParaResponse() {
        // given
        UUID id = UUID.randomUUID();
        HistoricoMedico entidade = criarHistoricoCompleto(id);

        // when
        HistoricoMedicoResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.pacienteNome()).isEqualTo("João");
        assertThat(response.medicoNome()).isEqualTo("Dr. Carlos");
        assertThat(response.consultaStatus()).isEqualTo("REALIZADA");
        assertThat(response.diagnostico()).isEqualTo("Hipertensão");
    }

    @Test
    void deveMapearHistoricoDetalhadoComExames() {
        // given
        UUID id = UUID.randomUUID();
        HistoricoMedico entidade = criarHistoricoCompleto(id);
        ExameMedico exame = new ExameMedico();
        exame.setId(UUID.randomUUID());
        exame.setTipoExame("Hemograma");
        exame.setResultado("Normal");
        exame.setDataExame(LocalDateTime.now());
        entidade.setExames(List.of(exame));

        // when
        HistoricoMedicoDetalhadoResponse response = mapper.toDetalhadoOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.pacienteNome()).isEqualTo("João");
        assertThat(response.exames()).singleElement()
                .extracting(exameResponse -> exameResponse.tipoExame())
                .isEqualTo("Hemograma");
    }

    @Test
    void deveMapearListasDeHistoricos() {
        // given
        HistoricoMedico entidade = criarHistoricoCompleto(UUID.randomUUID());

        // when
        List<HistoricoMedicoResponse> responses = mapper.toOutputList(List.of(entidade));
        List<HistoricoMedicoDetalhadoResponse> detalhados = mapper.toDetalhadoOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(HistoricoMedicoResponse::pacienteNome).isEqualTo("João");
        assertThat(detalhados).singleElement()
                .extracting(HistoricoMedicoDetalhadoResponse::medicoNome)
                .isEqualTo("Dr. Carlos");
    }

    private HistoricoMedico criarHistoricoCompleto(UUID id) {
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        Medico medico = new Medico();
        medico.setNome("Dr. Carlos");
        Consulta consulta = new Consulta();
        consulta.setStatus(Consulta.StatusConsulta.REALIZADA);

        HistoricoMedico entidade = new HistoricoMedico();
        entidade.setId(id);
        entidade.setPaciente(paciente);
        entidade.setMedico(medico);
        entidade.setConsulta(consulta);
        entidade.setDiagnostico("Hipertensão");
        entidade.setPrescricao("Losartana");
        entidade.setDataRegistro(LocalDateTime.now());
        return entidade;
    }
}
