package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecepcionistaMapperTest {

    private final RecepcionistaMapper mapper = Mappers.getMapper(RecepcionistaMapper.class);

    @Test
    void deveMapearRequestComStatusParaEntidade() {
        // given
        RecepcionistaRequest request = new RecepcionistaRequest("12345678901", "Ana", "(11) 91234-5678",
                LocalDate.of(2023, 1, 1), true, new UsuarioRequest("ana@example.com", "123456"));

        // when
        Recepcionista entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.getNome()).isEqualTo("Ana");
        assertThat(entidade.getCpf()).isEqualTo("12345678901");
        assertThat(entidade.isStatus()).isTrue();
        assertThat(entidade.getUsuario().getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    void deveMapearRequestSemStatusESemUsuarioParaEntidade() {
        // given
        RecepcionistaRequest request = new RecepcionistaRequest("12345678901", "Ana", null,
                LocalDate.of(2023, 1, 1), null, null);

        // when
        Recepcionista entidade = mapper.toEntity(request);

        // then
        assertThat(entidade.isStatus()).isFalse();
        assertThat(entidade.getUsuario()).isNull();
    }

    @Test
    void deveMapearEntidadeParaResponse() {
        // given
        UUID id = UUID.randomUUID();
        Recepcionista entidade = new Recepcionista();
        entidade.setId(id);
        entidade.setNome("Ana");
        entidade.setCpf("12345678901");
        entidade.setDataContratacao(LocalDate.of(2023, 1, 1));
        entidade.setStatus(true);

        // when
        RecepcionistaResponse response = mapper.toOutput(entidade);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Ana");
        assertThat(response.status()).isTrue();
    }

    @Test
    void deveMapearListaDeEntidadesParaResponses() {
        // given
        Recepcionista entidade = new Recepcionista();
        entidade.setNome("Ana");

        // when
        List<RecepcionistaResponse> responses = mapper.toOutputList(List.of(entidade));

        // then
        assertThat(responses).singleElement().extracting(RecepcionistaResponse::nome).isEqualTo("Ana");
    }

    @Test
    void deveRetornarNuloParaEntradasNulas() {
        // when / then
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toOutput(null)).isNull();
        assertThat(mapper.toOutputList(null)).isNull();
    }
}
