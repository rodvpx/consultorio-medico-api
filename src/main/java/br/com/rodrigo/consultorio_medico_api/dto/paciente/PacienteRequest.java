package br.com.rodrigo.consultorio_medico_api.dto.paciente;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres.")
        String nome,

        @NotBlank(message = "CPF é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "CPF inválido. Deve conter 11 dígitos, sem pontuação.")
        String cpf,

        @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX.")
        String telefone,

        @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres.")
        String endereco,

        @Past(message = "A data de nascimento deve ser no passado.")
        LocalDate dataNascimento,

        Paciente.Genero genero,

        @NotNull(message = "Usuário é obrigatório.")
        @Valid
        UsuarioRequest usuario
) {
}
