package br.com.rodrigo.consultorio_medico_api.dto.historico_medico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HistoricoMedicoRequest(
        @NotBlank(message = "Diagnóstico é obrigatório.")
        @Size(max = 255, message = "Diagnóstico deve ter no máximo 255 caracteres.")
        String diagnostico,

        @Size(max = 255, message = "Prescrição deve ter no máximo 255 caracteres.")
        String prescricao,

        @Size(max = 255, message = "Observações devem ter no máximo 255 caracteres.")
        String observacoes
) {
}
