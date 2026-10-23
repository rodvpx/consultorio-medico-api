package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExameMedicoMapperTest {

    private final ExameMedicoMapper mapper = Mappers.getMapper(ExameMedicoMapper.class);

    @Test
    void deveMapearRequestParaEntidade() {
        // given
        LocalDateTime dataExame = LocalDateTime.now();
        ExameMedicoRequest request = new ExameMedicoRequest("Hemograma", "Normal", dataExame,
                "https://exemplo.com/exame.pdf");

        // when
        ExameMedico entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getTipoExame()).isEqualTo("Hemograma");
        assertThat(entidade.getResultado()).isEqualTo("Normal");
        assertThat(entidade.getDataExame()).isEqualTo(dataExame);
        assertThat(entidade.getArquivoUrl()).isEqualTo("https://exemplo.com/exame.pdf");
    }

    @Test
    void deveMapearEntidadeParaResponse() {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime dataExame = LocalDateTime.now();
        ExameMedico entidade = new ExameMedico();
        entidade.setId(id);
        entidade.setTipoExame("Hemograma");
        entidade.setResultado("Normal");
        entidade.setDataExame(dataExame);

        // when
        ExameMedicoResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.tipoExame()).isEqualTo("Hemograma");
        assertThat(response.dataExame()).isEqualTo(dataExame);
    }

    @Test
    void deveMapearListaDeEntidadesParaResponses() {
        // given
        ExameMedico entidade = new ExameMedico();
        entidade.setTipoExame("Hemograma");

        // when
        List<ExameMedicoResponse> responses = mapper.toOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(ExameMedicoResponse::tipoExame).isEqualTo("Hemograma");
    }

}
