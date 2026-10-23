package br.com.rodrigo.consultorio_medico_api.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    void deveConverterValorPersistidoComPrefixoRole() {
        // when
        Role role = Role.fromValorPersistido("role_admin");

        // then
        assertThat(role).isEqualTo(Role.ADMIN);
    }

    @Test
    void deveConverterValorPersistidoSemPrefixo() {
        // when
        Role role = Role.fromValorPersistido(" MEDICO ");

        // then
        assertThat(role).isEqualTo(Role.MEDICO);
    }

    @Test
    void deveRetornarNuloParaValorNuloOuEmBranco() {
        // when / then
        assertThat(Role.fromValorPersistido(null)).isNull();
        assertThat(Role.fromValorPersistido("   ")).isNull();
    }

    @Test
    void deveFalharParaValorInvalido() {
        // when / then
        assertThatThrownBy(() -> Role.fromValorPersistido("INVALIDO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRetornarAuthorityComPrefixoRole() {
        // when / then
        assertThat(Role.ADMIN.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(Role.PACIENTE.getAuthority()).isEqualTo("ROLE_PACIENTE");
    }
}
