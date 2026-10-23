package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.usuario.UsuarioRequest;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Garante que a gravação do usuário e da entidade de domínio acontece na mesma
 * transação. Se o segundo {@code save} falhar, o usuário já gravado precisa ser
 * revertido para não bloquear o e-mail em uma nova tentativa.
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransacaoRollbackTest {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private RecepcionistaService recepcionistaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockitoSpyBean
    private PacienteRepository pacienteRepository;

    @MockitoSpyBean
    private RecepcionistaRepository recepcionistaRepository;

    @Test
    void deveReverterUsuarioQuandoSalvarPacienteFalha() {
        // given
        PacienteRequest input = new PacienteRequest(
                "Paciente Rollback",
                "11122233344",
                "(11) 91234-5678",
                "Rua Rollback, 1",
                LocalDate.of(1990, 1, 1),
                Paciente.Genero.MASCULINO,
                new UsuarioRequest("rollback.paciente@example.com", "123456"));
        doThrow(new DataIntegrityViolationException("falha simulada ao salvar paciente"))
                .when(pacienteRepository).save(any(Paciente.class));

        // when / then
        assertThatThrownBy(() -> pacienteService.save(input))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("falha simulada ao salvar paciente");

        verify(pacienteRepository).save(any(Paciente.class));
        assertThat(usuarioRepository.findByEmail("rollback.paciente@example.com")).isEmpty();
        assertThat(usuarioRepository.findAll())
                .extracting(Usuario::getEmail)
                .doesNotContain("rollback.paciente@example.com");
    }

    @Test
    void deveReverterUsuarioQuandoSalvarRecepcionistaFalha() {
        // given
        RecepcionistaRequest input = new RecepcionistaRequest(
                "55566677788",
                "Recepcionista Rollback",
                "(11) 91234-5678",
                LocalDate.of(2023, 1, 1),
                true,
                new UsuarioRequest("rollback.recepcionista@example.com", "123456"));
        doThrow(new DataIntegrityViolationException("falha simulada ao salvar recepcionista"))
                .when(recepcionistaRepository).save(any(Recepcionista.class));

        // when / then
        assertThatThrownBy(() -> recepcionistaService.save(input))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("falha simulada ao salvar recepcionista");

        verify(recepcionistaRepository).save(any(Recepcionista.class));
        assertThat(usuarioRepository.findByEmail("rollback.recepcionista@example.com")).isEmpty();
        assertThat(usuarioRepository.findAll())
                .extracting(Usuario::getEmail)
                .doesNotContain("rollback.recepcionista@example.com");
    }
}
