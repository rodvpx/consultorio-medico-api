package br.com.rodrigo.consultorio_medico_api.mapper;

import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoricoMedicoMapper {
    HistoricoMedico toEntity(HistoricoMedicoRequest input);

    HistoricoMedicoResponse toOutput(HistoricoMedico entity);

    List<HistoricoMedicoResponse> toOutputList(List<HistoricoMedico> entities);
}