package br.com.rodrigo.consultorio_medico_api.dto.recepcionista;

import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;

import java.time.LocalDate;

public record RecepcionistaRequest (
        String cpf,
        String nome,
        String telefone,
        LocalDate dataContratacao,
        Boolean status,
        UsuarioRequest usuario
){
}
