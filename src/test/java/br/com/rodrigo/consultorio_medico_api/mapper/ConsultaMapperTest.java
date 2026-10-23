package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultaMapperTest {

    private final ConsultaMapper mapper = Mappers.getMapper(ConsultaMapper.class);

    @Test
    void deveMapearRequestParaEntidade() {
        // given
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        ConsultaRequest request = new ConsultaRequest(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), dataHora, "Primeira consulta");

        // when
        Consulta entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getDataHora()).isEqualTo(dataHora);
        assertThat(entidade.getObservacoes()).isEqualTo("Primeira consulta");
    }

    @Test
    void deveMapearEntidadeComRelacionamentosParaResponse() {
        // given
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        Medico medico = new Medico();
        medico.setNome("Dr. Carlos");
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setNome("Ana");

        Consulta entidade = new Consulta();
        entidade.setId(id);
        entidade.setPaciente(paciente);
        entidade.setMedico(medico);
        entidade.setRecepcionista(recepcionista);
        entidade.setDataHora(LocalDateTime.now().plusDays(1));
        entidade.setStatus(Consulta.StatusConsulta.AGENDADA);
        entidade.setObservacoes("Primeira consulta");

        // when
        ConsultaResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.pacienteNome()).isEqualTo("João");
        assertThat(response.medicoNome()).isEqualTo("Dr. Carlos");
        assertThat(response.recepcionistaNome()).isEqualTo("Ana");
        assertThat(response.status()).isEqualTo("AGENDADA");
    }

    @Test
    void deveMapearListaDeEntidadesParaResponses() {
        // given
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        Consulta entidade = new Consulta();
        entidade.setPaciente(paciente);

        // when
        List<ConsultaResponse> responses = mapper.toOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(ConsultaResponse::pacienteNome).isEqualTo("João");
    }

}
