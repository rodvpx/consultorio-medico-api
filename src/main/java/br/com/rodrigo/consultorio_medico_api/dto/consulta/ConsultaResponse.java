package br.com.rodrigo.consultorio_medico_api.dto.consulta;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaResponse(
        UUID id,
        String pacienteNome,
        String medicoNome,
        String recepcionistaNome,
        LocalDateTime dataHora,
        String status,
        String motivoCancelamento,
        String observacoes
) {}
