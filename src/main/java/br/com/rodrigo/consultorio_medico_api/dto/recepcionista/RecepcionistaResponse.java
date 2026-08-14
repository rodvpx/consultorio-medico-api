package br.com.rodrigo.consultorio_medico_api.dto.recepcionista;

import java.time.LocalDate;

public record RecepcionistaResponse (
        String nome,
        LocalDate dataContratacao,
        boolean status
) {
}
