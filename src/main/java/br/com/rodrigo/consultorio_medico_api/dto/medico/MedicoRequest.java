package br.com.rodrigo.consultorio_medico_api.dto.medico;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;

import java.time.LocalDate;

public record MedicoRequest(
        String nome,
        String telefone,
        String crm,
        Medico.Especialidade especialidade,
        LocalDate dataContratacao,
        Boolean status,
        UsuarioRequest usuario
) {
}
