package br.com.rodrigo.consultorio_medico_api.dto.paciente;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;

import java.time.LocalDate;

public record PacienteRequest(
        String nome,
        String cpf,
        String telefone,
        String endereco,
        LocalDate dataNascimento,
        Paciente.Genero genero,
        UsuarioRequest usuario
) {
}
