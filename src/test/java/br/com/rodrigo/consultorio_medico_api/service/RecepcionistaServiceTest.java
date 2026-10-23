package br.com.rodrigo.consultorio_medico_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.mapper.RecepcionistaMapper;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RecepcionistaServiceTest {

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecepcionistaMapper recepcionistaMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RecepcionistaService service;

    @Test
    void deveLancarConflitoQuandoCpfJaExiste() {
        // given
        RecepcionistaRequest input = criarRequest();

        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    void deveLancarConflitoQuandoEmailJaExiste() {
        // given
        RecepcionistaRequest input = criarRequest();

        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    void deveSalvarRecepcionistaComSenhaCriptografadaEPapelRecepcionista() {
        // given
        RecepcionistaRequest input = criarRequest();
        Recepcionista recepcionista = new Recepcionista();
        RecepcionistaResponse response = new RecepcionistaResponse("Nome Teste", LocalDate.now(), true);

        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(false);
        when(passwordEncoder.encode(input.usuario().senha())).thenReturn("senha-criptografada");
        when(recepcionistaMapper.toEntity(input)).thenReturn(recepcionista);
        when(recepcionistaRepository.save(recepcionista)).thenReturn(recepcionista);
        when(recepcionistaMapper.toOutput(recepcionista)).thenReturn(response);

        // when
        RecepcionistaResponse resultado = service.save(input);

        // then
        assertEquals(response, resultado);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioCapturado = usuarioCaptor.getValue();
        assertEquals(input.usuario().email(), usuarioCapturado.getEmail());
        assertEquals("senha-criptografada", usuarioCapturado.getSenha());
        assertEquals(Role.RECEPCIONISTA, usuarioCapturado.getRole());

        ArgumentCaptor<Recepcionista> recepcionistaCaptor = ArgumentCaptor.forClass(Recepcionista.class);
        verify(recepcionistaRepository).save(recepcionistaCaptor.capture());
        assertEquals(usuarioCapturado, recepcionistaCaptor.getValue().getUsuario());
    }

    private RecepcionistaRequest criarRequest() {
        UsuarioRequest usuario = new UsuarioRequest("teste@email.com", "123456");
        return new RecepcionistaRequest(
                "12345678901",
                "Nome Teste",
                "(12) 34567-8901",
                LocalDate.now(),
                true,
                usuario);
    }
}
