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
        MedicoResponse response = new MedicoResponse("Dr. Teste", "123456", Medico.Especialidade.CARDIOLOGIA,
                LocalDate.of(2023, 1, 1), true);

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

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals(Role.MEDICO, usuarioCaptor.getValue().getRole());
        assertEquals("senha-criptografada", usuarioCaptor.getValue().getSenha());
    }

    @Test
    void deveLancarConflitoQuandoCrmJaExiste() {
        // given
        MedicoRequest input = criarRequest();
        when(medicoRepository.existsByCrm(input.crm())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveLancarConflitoQuandoEmailJaExiste() {
        // given
        MedicoRequest input = criarRequest();
        when(medicoRepository.existsByCrm(input.crm())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(true);

        // when / then
        assertThrows(ConflitoException.class, () -> service.save(input));
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarMedicoPorCrmInexistente() {
        // given
        when(medicoRepository.findByCrm("123456")).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.findByCrm("123456"));
    }

    @Test
    void deveLancarNaoEncontradoQuandoBuscaPorNomeNaoRetornaMedicos() {
        // given
        when(medicoRepository.findByNome("Inexistente")).thenReturn(List.of());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.findByNome("Inexistente"));
    }

    @Test
    void deveLancarNaoEncontradoAoAtualizarMedicoInexistente() {
        // given
        UUID id = UUID.randomUUID();
        when(medicoRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(RecursoNaoEncontradoException.class, () -> service.update(id, criarRequest()));
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
