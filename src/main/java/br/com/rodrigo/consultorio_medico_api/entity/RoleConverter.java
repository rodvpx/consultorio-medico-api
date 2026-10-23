package br.com.rodrigo.consultorio_medico_api.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte o enum {@link Role} para o formato histórico {@code ROLE_ADMIN}.
 * A leitura aceita tanto {@code ROLE_ADMIN} quanto {@code ADMIN}.
 */
@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    private static final String PREFIXO = "ROLE_";

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? null : PREFIXO + role.name();
    }

    @Override
    public Role convertToEntityAttribute(String valor) {
        return Role.fromValorPersistido(valor);
    }
}
