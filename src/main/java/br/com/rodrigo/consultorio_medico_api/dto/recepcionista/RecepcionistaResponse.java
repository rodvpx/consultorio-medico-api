package br.com.rodrigo.consultorio_medico_api.dto.recepcionista;

import java.time.LocalDate;
import java.util.UUID;

public record RecepcionistaResponse(
        UUID id,
        String nome,
        String cpf,
        LocalDate dataContratacao,
        boolean status
) {
}
