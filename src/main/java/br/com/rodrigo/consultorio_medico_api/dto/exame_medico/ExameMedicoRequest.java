package br.com.rodrigo.consultorio_medico_api.dto.exame_medico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ExameMedicoRequest(
        @NotBlank(message = "Tipo de exame é obrigatório.")
        @Size(max = 255, message = "Tipo de exame deve ter no máximo 255 caracteres.")
        String tipoExame,

        @NotBlank(message = "Resultado é obrigatório.")
        @Size(max = 255, message = "Resultado deve ter no máximo 255 caracteres.")
        String resultado,

        @NotNull(message = "Data do exame é obrigatória.")
        LocalDateTime dataExame,

        @Size(max = 500, message = "URL do arquivo deve ter no máximo 500 caracteres.")
        String arquivoUrl
) {
}
