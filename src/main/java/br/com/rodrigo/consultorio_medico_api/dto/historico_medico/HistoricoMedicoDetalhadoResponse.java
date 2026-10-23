package br.com.rodrigo.consultorio_medico_api.dto.historico_medico;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistoricoMedicoDetalhadoResponse(
        UUID id,
        String pacienteNome,
        String medicoNome,
        String consultaStatus,
        String diagnostico,
        String prescricao,
        String observacoes,
        LocalDateTime dataRegistro,
        List<ExameMedicoResponse> exames
) {}
