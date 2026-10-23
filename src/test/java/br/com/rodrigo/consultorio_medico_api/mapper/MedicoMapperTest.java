package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MedicoMapperTest {

    private final MedicoMapper mapper = Mappers.getMapper(MedicoMapper.class);

    @Test
    void deveMapearRequestParaEntidadeComUsuario() {
        // given
        MedicoRequest request = new MedicoRequest("Dr. Carlos", "(11) 91234-5678", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true,
                new UsuarioRequest("carlos@example.com", "123456"));

        // when
        Medico entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getNome()).isEqualTo("Dr. Carlos");
        assertThat(entidade.getCrm()).isEqualTo("123456");
        assertThat(entidade.getEspecialidade()).isEqualTo(Medico.Especialidade.CARDIOLOGIA);
        assertThat(entidade.getUsuario().getEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    void deveMapearEntidadeParaResponse() {
        // given
        UUID id = UUID.randomUUID();
        Medico entidade = new Medico();
        entidade.setId(id);
        entidade.setNome("Dr. Carlos");
        entidade.setCrm("123456");
        entidade.setEspecialidade(Medico.Especialidade.CARDIOLOGIA);
        entidade.setDataContratacao(LocalDate.of(2023, 1, 1));
        entidade.setStatus(true);

        // when
        MedicoResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Dr. Carlos");
        assertThat(response.crm()).isEqualTo("123456");
        assertThat(response.status()).isTrue();
    }

    @Test
    void deveMapearListaDeEntidadesParaResponses() {
        // given
        Medico entidade = new Medico();
        entidade.setNome("Dr. Carlos");

        // when
        List<MedicoResponse> responses = mapper.toOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(MedicoResponse::nome).isEqualTo("Dr. Carlos");
    }

}
