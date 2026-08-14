package br.com.rodrigo.consultorio_medico_api.dto.consulta;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaRequest(
        UUID pacienteId,
        UUID medicoId,
        UUID recepcionistaId,
        LocalDateTime dataHora,
        String observacoes
) {}
