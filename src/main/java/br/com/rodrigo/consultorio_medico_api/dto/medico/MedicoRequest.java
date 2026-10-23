package br.com.rodrigo.consultorio_medico_api.dto.medico;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MedicoRequest(
        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres.")
        String nome,

        @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX.")
        String telefone,

        @NotBlank(message = "CRM é obrigatório.")
        @Pattern(regexp = "\\d{4,6}", message = "CRM inválido. Deve conter de 4 a 6 dígitos.")
        String crm,

        @NotNull(message = "Especialidade é obrigatória.")
        Medico.Especialidade especialidade,

        @NotNull(message = "Data de contratação é obrigatória.")
        @PastOrPresent(message = "A data de contratação não pode ser no futuro.")
        LocalDate dataContratacao,

        @NotNull(message = "Status é obrigatório.")
        Boolean status,

        @NotNull(message = "Usuário é obrigatório.")
        @Valid
        UsuarioRequest usuario
) {
}
