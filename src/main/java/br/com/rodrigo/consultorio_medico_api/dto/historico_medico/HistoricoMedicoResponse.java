package br.com.rodrigo.consultorio_medico_api.dto.historico_medico;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoMedicoResponse(
        UUID id,
        String pacienteNome,
        String medicoNome,
        String consultaStatus,
        String diagnostico,
        String prescricao,
        String observacoes,
        LocalDateTime dataRegistro
) {}