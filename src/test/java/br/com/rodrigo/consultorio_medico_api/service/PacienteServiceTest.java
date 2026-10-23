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
import java.util.List;
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
        PacienteResponse response = new PacienteResponse(UUID.randomUUID(), "Nome Teste", "12345678901",
                LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO);

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

        verify(passwordEncoder).encode("123456");
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("paciente@email.com", usuarioCaptor.getValue().getEmail());
        assertEquals("senha-criptografada", usuarioCaptor.getValue().getSenha());
        assertEquals(Role.PACIENTE, usuarioCaptor.getValue().getRole());

        ArgumentCaptor<Paciente> pacienteCaptor = ArgumentCaptor.forClass(Paciente.class);
        verify(pacienteRepository).save(pacienteCaptor.capture());
        assertEquals(usuarioCaptor.getValue(), pacienteCaptor.getValue().getUsuario());
    }

    @Test
    void deveLancarConflitoQuandoCpfJaExiste() {
        // given
        PacienteRequest input = criarRequest();
        when(pacienteRepository.existsByCpf(input.cpf())).thenReturn(true);

        // when / then
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.save(input));
        assertEquals("Já existe um paciente cadastrado com o CPF informado.", ex.getMessage());
        verify(usuarioRepository, never()).existsByEmail(any());
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
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.save(input));
        assertEquals("Já existe um usuário cadastrado com o e-mail informado.", ex.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveAtualizarPacienteExistente() {
        // given
        UUID id = UUID.randomUUID();
        PacienteRequest input = criarRequest();
        Paciente existente = new Paciente();
        existente.setId(id);
        existente.setCpf(input.cpf());
        PacienteResponse response = new PacienteResponse(id, input.nome(), input.cpf(),
                input.dataNascimento(), input.genero());

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(existente));
        when(pacienteRepository.save(existente)).thenReturn(existente);
        when(pacienteMapper.toOutput(existente)).thenReturn(response);

        // when
        PacienteResponse resultado = service.update(id, input);

        // then
        assertEquals(response, resultado);
        assertEquals("Nome Teste", existente.getNome());
        assertEquals("12345678901", existente.getCpf());
        assertEquals("(11) 91234-5678", existente.getTelefone());
        assertEquals("Rua Teste, 1", existente.getEndereco());
        assertEquals(LocalDate.of(1990, 1, 1), existente.getDataNascimento());
        assertEquals(Paciente.Genero.MASCULINO, existente.getGenero());
        verify(pacienteRepository, never()).existsByCpf(any());
        verify(pacienteRepository).save(existente);
    }

    @Test
    void deveLancarConflitoAoAtualizarPacienteComCpfDeOutroPaciente() {
        // given
        UUID id = UUID.randomUUID();
        PacienteRequest input = criarRequest();
        Paciente existente = new Paciente();
        existente.setCpf("99999999999");

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(existente));
        when(pacienteRepository.existsByCpf(input.cpf())).thenReturn(true);

        // when / then
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.update(id, input));
        assertEquals("Já existe um paciente cadastrado com o CPF informado.", ex.getMessage());
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveBuscarPacientePorCpfExistente() {
        // given
        Paciente paciente = new Paciente();
        PacienteResponse response = new PacienteResponse(UUID.randomUUID(), "João", "12345678901",
                LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO);

        when(pacienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(paciente));
        when(pacienteMapper.toOutput(paciente)).thenReturn(response);

        // when
        PacienteResponse resultado = service.findByCpf("12345678901");

        // then
        assertEquals(response, resultado);
    }

    @Test
    void deveListarTodosOsPacientes() {
        // given
        Paciente paciente = new Paciente();
        PacienteResponse response = new PacienteResponse(UUID.randomUUID(), "João", "12345678901",
                LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO);

        when(pacienteRepository.findAll()).thenReturn(List.of(paciente));
        when(pacienteMapper.toOutputList(List.of(paciente))).thenReturn(List.of(response));

        // when
        List<PacienteResponse> resultado = service.findAll();

        // then
        assertEquals(List.of(response), resultado);
    }

    @Test
    void deveExcluirPacienteEUsuarioAssociado() {
        // given
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        // when
        service.excluirPaciente(id);

        // then
        verify(usuarioRepository).delete(usuario);
        verify(pacienteRepository).delete(paciente);
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarPacientePorCpfInexistente() {
        // given
        when(pacienteRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.findByCpf("12345678901"));
        assertEquals("Paciente com CPF '12345678901' não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarPacienteInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.update(id, criarRequest()));
        assertEquals("Paciente não encontrado.", ex.getMessage());
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveLancarNaoEncontradoAoExcluirPacienteInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.excluirPaciente(id));
        assertEquals("Paciente não encontrado.", ex.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
        verify(pacienteRepository, never()).delete(any(Paciente.class));
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
