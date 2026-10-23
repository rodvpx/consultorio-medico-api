package br.com.rodrigo.consultorio_medico_api.dto.paciente;

import br.com.rodrigo.consultorio_medico_api.entity.Paciente;

import java.time.LocalDate;
import java.util.UUID;

public record PacienteResponse(
        UUID id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        Paciente.Genero genero
) {
}
