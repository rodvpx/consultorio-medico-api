package br.com.rodrigo.consultorio_medico_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
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
        RecepcionistaResponse response = new RecepcionistaResponse(java.util.UUID.randomUUID(), "Nome Teste",
                "12345678901", LocalDate.now(), true);

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

    @Test
    void deveAtualizarRecepcionistaExistente() {
        // given
        UUID id = UUID.randomUUID();
        RecepcionistaRequest input = criarRequest();
        Recepcionista existente = new Recepcionista();
        existente.setId(id);
        existente.setCpf(input.cpf());
        RecepcionistaResponse response = new RecepcionistaResponse(id, input.nome(), input.cpf(),
                input.dataContratacao(), input.status());

        when(recepcionistaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(recepcionistaRepository.save(existente)).thenReturn(existente);
        when(recepcionistaMapper.toOutput(existente)).thenReturn(response);

        // when
        RecepcionistaResponse resultado = service.update(id, input);

        // then
        assertEquals(response, resultado);
        assertEquals("Nome Teste", existente.getNome());
        verify(recepcionistaRepository, never()).existsByCpf(any());
    }

    @Test
    void deveLancarConflitoAoAtualizarRecepcionistaComCpfDeOutro() {
        // given
        UUID id = UUID.randomUUID();
        RecepcionistaRequest input = criarRequest();
        Recepcionista existente = new Recepcionista();
        existente.setCpf("99999999999");

        when(recepcionistaRepository.findById(id)).thenReturn(Optional.of(existente));
        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.update(id, input));
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarRecepcionistaInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(recepcionistaRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.update(id, criarRequest()));
    }

    @Test
    void deveBuscarRecepcionistaPorCpfExistente() {
        // given
        Recepcionista recepcionista = new Recepcionista();
        RecepcionistaResponse response = new RecepcionistaResponse(UUID.randomUUID(), "Ana",
                "12345678901", LocalDate.now(), true);

        when(recepcionistaRepository.findByCpf("12345678901")).thenReturn(Optional.of(recepcionista));
        when(recepcionistaMapper.toOutput(recepcionista)).thenReturn(response);

        // when
        RecepcionistaResponse resultado = service.findByCpf("12345678901");

        // then
        assertEquals(response, resultado);
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarRecepcionistaPorCpfInexistente() {
        // given
        when(recepcionistaRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.findByCpf("12345678901"));
    }

    @Test
    void deveListarTodosOsRecepcionistas() {
        // given
        Recepcionista recepcionista = new Recepcionista();
        RecepcionistaResponse response = new RecepcionistaResponse(UUID.randomUUID(), "Ana",
                "12345678901", LocalDate.now(), true);

        when(recepcionistaRepository.findAll()).thenReturn(List.of(recepcionista));
        when(recepcionistaMapper.toOutputList(List.of(recepcionista))).thenReturn(List.of(response));

        // when
        List<RecepcionistaResponse> resultado = service.findAll();

        // then
        assertEquals(List.of(response), resultado);
    }

    @Test
    void deveExcluirRecepcionistaEUsuarioAssociado() {
        // given
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setUsuario(usuario);

        when(recepcionistaRepository.findById(id)).thenReturn(Optional.of(recepcionista));

        // when
        service.excluirRecepcionista(id);

        // then
        verify(usuarioRepository).delete(usuario);
        verify(recepcionistaRepository).delete(recepcionista);
    }

    @Test
    void deveLancarNaoEncontradoAoExcluirRecepcionistaInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(recepcionistaRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluirRecepcionista(id));
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
