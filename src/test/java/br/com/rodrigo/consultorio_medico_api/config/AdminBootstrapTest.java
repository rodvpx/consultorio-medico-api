package br.com.rodrigo.consultorio_medico_api.config;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void deveIgnorarQuandoBootstrapDesabilitado() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                "admin@example.com", "senha", false);

        // when
        bootstrap.run(null);

        // then
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveIgnorarQuandoEmailNaoConfigurado() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                null, "senha", true);

        // when
        bootstrap.run(null);

        // then
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveIgnorarQuandoEmailEmBranco() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                "   ", "senha", true);

        // when
        bootstrap.run(null);

        // then
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveIgnorarQuandoSenhaNaoConfigurada() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                "admin@example.com", null, true);

        // when
        bootstrap.run(null);

        // then
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveIgnorarQuandoSenhaEmBranco() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                "admin@example.com", "   ", true);

        // when
        bootstrap.run(null);

        // then
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveIgnorarQuandoAdminJaExiste() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                "admin@example.com", "senha", true);
        when(usuarioRepository.existsByEmail("admin@example.com")).thenReturn(true);

        // when
        bootstrap.run(null);

        // then
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveCriarAdminQuandoNaoExiste() {
        // given
        AdminBootstrap bootstrap = new AdminBootstrap(usuarioRepository, passwordEncoder,
                " admin@example.com ", "senha", true);
        when(usuarioRepository.existsByEmail(" admin@example.com ")).thenReturn(false);
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        // when
        bootstrap.run(null);

        // then
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("admin@example.com");
        assertThat(captor.getValue().getSenha()).isEqualTo("hash");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
    }
}
