package br.com.rodrigo.consultorio_medico_api.dto.exame_medico;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExameMedicoResponse(
        UUID id,
        String tipoExame,
        String resultado,
        LocalDateTime dataExame,
        String arquivoUrl
) {}