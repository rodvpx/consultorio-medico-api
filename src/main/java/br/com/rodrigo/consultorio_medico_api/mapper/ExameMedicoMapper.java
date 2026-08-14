package br.com.rodrigo.consultorio_medico_api.mapper;


import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.entity.ExameMedico;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExameMedicoMapper {
    ExameMedico toEntity(ExameMedicoRequest input);

    ExameMedicoResponse toOutput(ExameMedico entity);

    List<ExameMedicoResponse> toOutputList(List<ExameMedico> entities);
}