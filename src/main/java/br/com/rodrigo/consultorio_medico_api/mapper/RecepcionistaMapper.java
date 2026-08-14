package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecepcionistaMapper {
    Recepcionista toEntity(RecepcionistaRequest input);

    RecepcionistaResponse toOutput(Recepcionista entity);

    List<RecepcionistaResponse> toOutputList(List<Recepcionista> entities);
}