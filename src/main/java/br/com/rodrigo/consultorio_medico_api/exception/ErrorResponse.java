package br.com.rodrigo.consultorio_medico_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp,
        Map<String, String> campos
) {

    public static ErrorResponse de(int status, String mensagem) {
        return new ErrorResponse(status, mensagem, LocalDateTime.now(), null);
    }

    public static ErrorResponse de(int status, String mensagem, Map<String, String> campos) {
        return new ErrorResponse(status, mensagem, LocalDateTime.now(), campos);
    }
}
