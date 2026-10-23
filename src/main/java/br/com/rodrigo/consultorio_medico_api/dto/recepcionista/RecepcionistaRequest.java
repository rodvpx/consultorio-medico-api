package br.com.rodrigo.consultorio_medico_api.dto.recepcionista;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RecepcionistaRequest(
        @NotBlank(message = "CPF é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "CPF inválido. Deve conter 11 dígitos, sem pontuação.")
        String cpf,

        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres.")
        String nome,

        @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX.")
        String telefone,

        @PastOrPresent(message = "A data de contratação não pode ser no futuro.")
        LocalDate dataContratacao,

        @NotNull(message = "Status é obrigatório.")
        Boolean status,

        @NotNull(message = "Usuário é obrigatório.")
        @Valid
        UsuarioRequest usuario
) {
}
