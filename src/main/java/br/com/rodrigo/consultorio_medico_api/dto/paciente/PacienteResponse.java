package br.com.rodrigo.consultorio_medico_api.dto.paciente;

import br.com.rodrigo.consultorio_medico_api.entity.Paciente;

import java.time.LocalDate;

public record PacienteResponse(
        String nome,
        LocalDate dataNascimento,
        Paciente.Genero genero
) {
}
