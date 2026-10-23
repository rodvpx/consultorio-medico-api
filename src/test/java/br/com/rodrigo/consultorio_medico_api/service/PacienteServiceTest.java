package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.PacienteMapper;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PacienteMapper pacienteMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PacienteService service;

    @Test
    void deveCadastrarPacienteComSenhaCriptografadaEPapelPaciente() {
        // given
        PacienteRequest input = criarRequest();
        Paciente paciente = new Paciente();
        PacienteResponse response = new PacienteResponse("Nome Teste", LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO);

        when(pacienteRepository.existsByCpf(input.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(false);
        when(passwordEncoder.encode(input.usuario().senha())).thenReturn("senha-criptografada");
        when(pacienteMapper.toEntity(input)).thenReturn(paciente);
        when(pacienteRepository.save(paciente)).thenReturn(paciente);
        when(pacienteMapper.toOutput(paciente)).thenReturn(response);

        // when
        PacienteResponse resultado = service.save(input);

        // then
        assertEquals(response, resultado);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("senha-criptografada", usuarioCaptor.getValue().getSenha());
        assertEquals(Role.PACIENTE, usuarioCaptor.getValue().getRole());
    }

    @Test
    void deveLancarConflitoQuandoCpfJaExiste() {
        // given
        PacienteRequest input = criarRequest();
        when(pacienteRepository.existsByCpf(input.cpf())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveLancarConflitoQuandoEmailJaExiste() {
        // given
        PacienteRequest input = criarRequest();
        when(pacienteRepository.existsByCpf(input.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarPacientePorCpfInexistente() {
        // given
        when(pacienteRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.findByCpf("12345678901"));
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarPacienteInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.update(id, criarRequest()));
    }

    @Test
    void deveLancarNaoEncontradoAoExcluirPacienteInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluirPaciente(id));
    }

    private PacienteRequest criarRequest() {
        return new PacienteRequest(
                "Nome Teste",
                "12345678901",
                "(11) 91234-5678",
                "Rua Teste, 1",
                LocalDate.of(1990, 1, 1),
                Paciente.Genero.MASCULINO,
                new UsuarioRequest("paciente@email.com", "123456"));
    }
}
