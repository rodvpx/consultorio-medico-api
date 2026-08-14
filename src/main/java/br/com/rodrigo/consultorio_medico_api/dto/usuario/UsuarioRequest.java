package br.com.rodrigo.consultorio_medico_api.dto.usuario;

public record UsuarioRequest(
        String email,
        String senha
) {
}
