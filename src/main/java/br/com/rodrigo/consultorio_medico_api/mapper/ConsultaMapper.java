package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    Consulta toEntity(ConsultaRequest input);

    @Mapping(target = "pacienteNome", source = "paciente.nome")
    @Mapping(target = "medicoNome", source = "medico.nome")
    @Mapping(target = "recepcionistaNome", source = "recepcionista.nome")
    ConsultaResponse toOutput(Consulta entity);

    List<ConsultaResponse> toOutputList(List<Consulta> entities);
}
