package br.com.rodrigo.consultorio_medico_api.entity;

/**
 * Papéis de acesso da aplicação.
 *
 * <p>O valor persistido continua com o prefixo {@code ROLE_} (por exemplo,
 * {@code ROLE_RECEPCIONISTA}), mantendo compatibilidade com os registros criados
 * antes da introdução do enum. O papel {@code PACIENTE} é mantido apenas por
 * compatibilidade com usuários de pacientes já gravados.</p>
 */
public enum Role {
    ADMIN,
    MEDICO,
    RECEPCIONISTA,
    PACIENTE;

    public static Role fromValorPersistido(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String normalizado = valor.trim().toUpperCase();
        if (normalizado.startsWith("ROLE_")) {
            normalizado = normalizado.substring("ROLE_".length());
        }

        return Role.valueOf(normalizado);
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
