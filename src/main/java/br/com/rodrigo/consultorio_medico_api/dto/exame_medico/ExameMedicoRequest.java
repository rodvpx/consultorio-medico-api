package br.com.rodrigo.consultorio_medico_api.dto.exame_medico;

import java.time.LocalDateTime;

public record ExameMedicoRequest(
        String tipoExame,
        String resultado,
        LocalDateTime dataExame,
        String arquivoUrl
) {}