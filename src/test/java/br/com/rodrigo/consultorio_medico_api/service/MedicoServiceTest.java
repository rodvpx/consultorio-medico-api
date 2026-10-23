package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.MedicoMapper;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MedicoMapper medicoMapper;

    @InjectMocks
    private MedicoService service;

    @Test
    void deveCadastrarMedicoComSenhaCriptografadaEPapelMedico() {
        // given
        MedicoRequest input = criarRequest();
        Medico medico = new Medico();
        MedicoResponse response = new MedicoResponse(UUID.randomUUID(), "Dr. Teste", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true);

        when(medicoRepository.existsByCrm(input.crm())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(false);
        when(passwordEncoder.encode(input.usuario().senha())).thenReturn("senha-criptografada");
        when(medicoMapper.toEntity(input)).thenReturn(medico);
        when(medicoRepository.save(medico)).thenReturn(medico);
        when(medicoMapper.toOutput(medico)).thenReturn(response);

        // when
        MedicoResponse resultado = service.save(input);

        // then
        assertEquals(response, resultado);

        verify(passwordEncoder).encode("123456");
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("medico@email.com", usuarioCaptor.getValue().getEmail());
        assertEquals(Role.MEDICO, usuarioCaptor.getValue().getRole());
        assertEquals("senha-criptografada", usuarioCaptor.getValue().getSenha());

        ArgumentCaptor<Medico> medicoCaptor = ArgumentCaptor.forClass(Medico.class);
        verify(medicoRepository).save(medicoCaptor.capture());
        assertEquals(usuarioCaptor.getValue(), medicoCaptor.getValue().getUsuario());
    }

    @Test
    void deveLancarConflitoQuandoCrmJaExiste() {
        // given
        MedicoRequest input = criarRequest();
        when(medicoRepository.existsByCrm(input.crm())).thenReturn(true);

        // when / then
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.save(input));
        assertEquals("Já existe um médico cadastrado com o CRM informado.", ex.getMessage());
        verify(usuarioRepository, never()).existsByEmail(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void deveLancarConflitoQuandoEmailJaExiste() {
        // given
        MedicoRequest input = criarRequest();
        when(medicoRepository.existsByCrm(input.crm())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(true);

        // when / then
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.save(input));
        assertEquals("Já existe um usuário cadastrado com o e-mail informado.", ex.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void deveAtualizarMedicoExistente() {
        // given
        UUID id = UUID.randomUUID();
        MedicoRequest input = criarRequest();
        Medico existente = new Medico();
        existente.setId(id);
        existente.setCrm(input.crm());
        MedicoResponse response = new MedicoResponse(id, input.nome(), input.crm(),
                input.especialidade(), input.dataContratacao(), input.status());

        when(medicoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(medicoRepository.save(existente)).thenReturn(existente);
        when(medicoMapper.toOutput(existente)).thenReturn(response);

        // when
        MedicoResponse resultado = service.update(id, input);

        // then
        assertEquals(response, resultado);
        assertEquals("Dr. Teste", existente.getNome());
        assertEquals("(11) 91234-5678", existente.getTelefone());
        assertEquals("123456", existente.getCrm());
        assertEquals(Medico.Especialidade.CARDIOLOGIA, existente.getEspecialidade());
        assertEquals(LocalDate.of(2023, 1, 1), existente.getDataContratacao());
        assertTrue(existente.getStatus());
        verify(medicoRepository, never()).existsByCrm(any());
        verify(medicoRepository).save(existente);
    }

    @Test
    void deveLancarConflitoAoAtualizarMedicoComCrmDeOutroMedico() {
        // given
        UUID id = UUID.randomUUID();
        MedicoRequest input = criarRequest();
        Medico existente = new Medico();
        existente.setCrm("654321");

        when(medicoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(medicoRepository.existsByCrm(input.crm())).thenReturn(true);

        // when / then
        ConflitoException ex = assertThrows(ConflitoException.class, () -> service.update(id, input));
        assertEquals("Já existe um médico cadastrado com o CRM informado.", ex.getMessage());
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void deveBuscarMedicoPorCrmExistente() {
        // given
        Medico medico = new Medico();
        MedicoResponse response = new MedicoResponse(UUID.randomUUID(), "Dr. Teste", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true);

        when(medicoRepository.findByCrm("123456")).thenReturn(Optional.of(medico));
        when(medicoMapper.toOutput(medico)).thenReturn(response);

        // when
        MedicoResponse resultado = service.findByCrm("123456");

        // then
        assertEquals(response, resultado);
    }

    @Test
    void deveBuscarMedicosPorNome() {
        // given
        Medico medico = new Medico();
        MedicoResponse response = new MedicoResponse(UUID.randomUUID(), "Dr. Teste", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true);

        when(medicoRepository.findByNome("Dr. Teste")).thenReturn(List.of(medico));
        when(medicoMapper.toOutputList(List.of(medico))).thenReturn(List.of(response));

        // when
        List<MedicoResponse> resultado = service.findByNome("Dr. Teste");

        // then
        assertEquals(List.of(response), resultado);
    }

    @Test
    void deveListarTodosOsMedicos() {
        // given
        Medico medico = new Medico();
        MedicoResponse response = new MedicoResponse(UUID.randomUUID(), "Dr. Teste", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true);

        when(medicoRepository.findAll()).thenReturn(List.of(medico));
        when(medicoMapper.toOutputList(List.of(medico))).thenReturn(List.of(response));

        // when
        List<MedicoResponse> resultado = service.findAll();

        // then
        assertEquals(List.of(response), resultado);
    }

    @Test
    void deveExcluirMedicoEUsuarioAssociado() {
        // given
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        Medico medico = new Medico();
        medico.setUsuario(usuario);

        when(medicoRepository.findById(id)).thenReturn(Optional.of(medico));

        // when
        service.excluirMedico(id);

        // then
        verify(usuarioRepository).delete(usuario);
        verify(medicoRepository).delete(medico);
    }

    @Test
    void deveLancarNaoEncontradoAoExcluirMedicoInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(medicoRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.excluirMedico(id));
        assertEquals("Médico não encontrado.", ex.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
        verify(medicoRepository, never()).delete(any(Medico.class));
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarMedicoPorCrmInexistente() {
        // given
        when(medicoRepository.findByCrm("123456")).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.findByCrm("123456"));
        assertEquals("Médico com CRM '123456' não encontrado.", ex.getMessage());
    }

    @Test
    void deveLancarNaoEncontradoQuandoBuscaPorNomeNaoRetornaMedicos() {
        // given
        when(medicoRepository.findByNome("Inexistente")).thenReturn(List.of());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.findByNome("Inexistente"));
        assertEquals("Nenhum médico encontrado com o nome 'Inexistente'.", ex.getMessage());
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarMedicoInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(medicoRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.update(id, criarRequest()));
        assertEquals("Médico não encontrado.", ex.getMessage());
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    private MedicoRequest criarRequest() {
        return new MedicoRequest(
                "Dr. Teste",
                "(11) 91234-5678",
                "123456",
                Medico.Especialidade.CARDIOLOGIA,
                LocalDate.of(2023, 1, 1),
                true,
                new UsuarioRequest("medico@email.com", "123456"));
    }
}
