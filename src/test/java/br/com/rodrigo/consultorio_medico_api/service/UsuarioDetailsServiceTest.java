package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    void deveCarregarUsuarioComPapelCadastrado() {
        // given
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@example.com");
        usuario.setSenha("hash");
        usuario.setRole(Role.ADMIN);
        when(usuarioRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(usuario));

        // when
        UserDetails userDetails = service.loadUserByUsername("admin@example.com");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("hash");
        assertThat(userDetails.getAuthorities())
                .singleElement()
                .extracting(authority -> authority.getAuthority())
                .isEqualTo("ROLE_ADMIN");
        verify(usuarioRepository).findByEmail("admin@example.com");
    }

    @Test
    void deveUsarPapelPacienteQuandoUsuarioNaoTemPapel() {
        // given
        Usuario usuario = new Usuario();
        usuario.setEmail("legado@example.com");
        usuario.setSenha("hash");
        usuario.setRole(null);
        when(usuarioRepository.findByEmail("legado@example.com")).thenReturn(Optional.of(usuario));

        // when
        UserDetails userDetails = service.loadUserByUsername("legado@example.com");

        // then
        assertThat(userDetails.getAuthorities())
                .singleElement()
                .extracting(authority -> authority.getAuthority())
                .isEqualTo("ROLE_PACIENTE");
        verify(usuarioRepository).findByEmail("legado@example.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // given
        when(usuarioRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.loadUserByUsername("naoexiste@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado para o e-mail informado.");
        verify(usuarioRepository).findByEmail("naoexiste@example.com");
    }
}
