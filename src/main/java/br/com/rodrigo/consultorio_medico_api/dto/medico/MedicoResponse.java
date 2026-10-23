package br.com.rodrigo.consultorio_medico_api.dto.medico;

import br.com.rodrigo.consultorio_medico_api.entity.Medico;

import java.time.LocalDate;
import java.util.UUID;

public record MedicoResponse(
        UUID id,
        String nome,
        String crm,
        Medico.Especialidade especialidade,
        LocalDate dataContratacao,
        Boolean status
) {
}
