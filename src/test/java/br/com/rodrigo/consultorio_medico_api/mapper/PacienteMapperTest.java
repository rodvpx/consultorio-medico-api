package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PacienteMapperTest {

    private final PacienteMapper mapper = Mappers.getMapper(PacienteMapper.class);

    @Test
    void deveMapearRequestParaEntidadeComUsuario() {
        // given
        PacienteRequest request = new PacienteRequest("João", "12345678901", "(11) 91234-5678",
                "Rua Teste, 1", LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO,
                new UsuarioRequest("joao@example.com", "123456"));

        // when
        Paciente entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getNome()).isEqualTo("João");
        assertThat(entidade.getCpf()).isEqualTo("12345678901");
        assertThat(entidade.getGenero()).isEqualTo(Paciente.Genero.MASCULINO);
        assertThat(entidade.getUsuario().getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void deveMapearEntidadeParaResponseSemExporSenha() {
        // given
        UUID id = UUID.randomUUID();
        Paciente entidade = new Paciente();
        entidade.setId(id);
        entidade.setNome("João");
        entidade.setCpf("12345678901");
        entidade.setDataNascimento(LocalDate.of(1990, 1, 1));
        entidade.setGenero(Paciente.Genero.MASCULINO);

        // when
        PacienteResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("João");
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.genero()).isEqualTo(Paciente.Genero.MASCULINO);
    }

    @Test
    void deveMapearListaDeEntidadesParaResponses() {
        // given
        Paciente entidade = new Paciente();
        entidade.setNome("João");

        // when
        List<PacienteResponse> responses = mapper.toOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(PacienteResponse::nome).isEqualTo("João");
    }

}
