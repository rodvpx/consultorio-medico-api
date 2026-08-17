package br.com.rodrigo.consultorio_medico_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

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
public class RecepcionistaServiceTest {

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
    void shouldReturnNullWhenCpfAlreadyExists() {

        // Arrange - preparar o cenário criando os dados reais para os testes.
        UsuarioRequest usuario = new UsuarioRequest("teste@email.com", "123456");

        RecepcionistaRequest input = new RecepcionistaRequest(
                "12345678901",
                "Nome Teste",
                "(12) 34567-8901",
                LocalDate.now(),
                true,
                usuario);

        // Aqui configuramos o mock, para simular o estado do cpf: true = existe, false
        // = não existe
        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(true);

        // Act - executar o comportamento, a ação que estamos testando (no caso estamos
        // testando o metodo save do service)
        // dessa forma o resultado da ação real executada no nosso service
        // (service.save)
        // lá retorna um resultado, esse é armazenado na nossa variavel result
        RecepcionistaResponse result = service.save(input);

        // Assert - verificar o resultado, o método (service.save) produziu o resultado
        // esperado?
        // no nosso caso aqui, configuramos o mock com true, ou seja o CPF já existe
        // nisso o resultado esperado é null (de acordo com nosso metodo service.save
        // foi configurado)
        // dependendo do resultado esperado mudamos o metodo assert (Null, Equals,
        // NotNull, True, False, Throws...)
        assertNull(result);

        // Verify - verificar interações
        // Aqui verificamos se as dependências foram chamadas como deveriam.
        // Como o CPF já existe, o Service deve retornar antes de realizar os saves.
        // Portanto, nenhum Usuario deve ser salvo e nenhuma Recepcionista deve ser
        // salva.
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));

    }

    @Test
    void shouldReturnNullWhenEmailAlreadyExists() {

        UsuarioRequest usuario = new UsuarioRequest("teste@emai.com", "123456");

        RecepcionistaRequest input = new RecepcionistaRequest(
                "12345678901",
                "Nome Teste",
                "(12) 34567-8901",
                LocalDate.now(),
                true,
                usuario);

        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(false);

        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(true);

        RecepcionistaResponse result = service.save(input);

        assertNull(result);

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    void shouldSaveRecepcionistaSuccessfully() {

        UsuarioRequest usuario = new UsuarioRequest("teste@emai.com", "123456");

        RecepcionistaRequest input = new RecepcionistaRequest(
                "12345678901",
                "Nome Teste",
                "(12) 34567-8901",
                LocalDate.now(),
                true,
                usuario);

        Recepcionista recepcionista = new Recepcionista();

        RecepcionistaResponse response = new RecepcionistaResponse(
                "Nome Teste",
                LocalDate.now(),
                true);

        when(recepcionistaRepository.existsByCpf(input.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(input.usuario().email())).thenReturn(false);

        var encryptedPassword = "senha-criptografada";
        when(passwordEncoder.encode(input.usuario().senha())).thenReturn(encryptedPassword);

        when(recepcionistaMapper.toEntity(input)).thenReturn(recepcionista);
        when(recepcionistaRepository.save(recepcionista)).thenReturn(recepcionista);
        when(recepcionistaMapper.toOutput(recepcionista)).thenReturn(response);

        RecepcionistaResponse result = service.save(input);

        assertEquals(response, result);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioCapturado = usuarioCaptor.getValue();

        assertEquals(input.usuario().email(), usuarioCapturado.getEmail());
        assertEquals(encryptedPassword, usuarioCapturado.getSenha());
        assertEquals("ROLE_RECEPCIONISTA", usuarioCapturado.getRole());

        ArgumentCaptor<Recepcionista> recepcionistaCaptor = ArgumentCaptor.forClass(Recepcionista.class);

        verify(recepcionistaRepository).save(recepcionistaCaptor.capture());

        Recepcionista recepcionistaCapturado = recepcionistaCaptor.getValue();

        assertEquals(usuarioCapturado, recepcionistaCapturado.getUsuario());

    }

}
