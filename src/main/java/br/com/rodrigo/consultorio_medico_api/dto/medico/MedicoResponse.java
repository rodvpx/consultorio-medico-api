package br.com.rodrigo.consultorio_medico_api.dto.medico;

import br.com.rodrigo.consultorio_medico_api.entity.Medico;

import java.time.LocalDate;

public record MedicoResponse(
        String nome,
        String crm,
        Medico.Especialidade especialidade,
        LocalDate dataContratacao,
        Boolean status
) {
}
