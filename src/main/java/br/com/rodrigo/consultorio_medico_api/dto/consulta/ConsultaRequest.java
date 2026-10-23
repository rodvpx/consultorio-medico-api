package br.com.rodrigo.consultorio_medico_api.dto.consulta;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaRequest(
        @NotNull(message = "Paciente é obrigatório.")
        UUID pacienteId,

        @NotNull(message = "Médico é obrigatório.")
        UUID medicoId,

        @NotNull(message = "Recepcionista é obrigatório.")
        UUID recepcionistaId,

        @NotNull(message = "Data e hora são obrigatórias.")
        @FutureOrPresent(message = "A data da consulta não pode ser no passado.")
        LocalDateTime dataHora,

        @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres.")
        String observacoes
) {
}
