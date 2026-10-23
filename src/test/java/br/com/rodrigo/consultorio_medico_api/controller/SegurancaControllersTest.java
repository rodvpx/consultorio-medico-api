package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.config.SecurityConfig;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoDetalhadoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.service.ConsultaService;
import br.com.rodrigo.consultorio_medico_api.service.ExameMedicoService;
import br.com.rodrigo.consultorio_medico_api.service.HistoricoMedicoService;
import br.com.rodrigo.consultorio_medico_api.service.MedicoService;
import br.com.rodrigo.consultorio_medico_api.service.PacienteService;
import br.com.rodrigo.consultorio_medico_api.service.RecepcionistaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        PacienteController.class,
        MedicoController.class,
        RecepcionistaController.class,
        ConsultaController.class,
        HistoricoMedicoController.class,
        ExameMedicoController.class
})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SegurancaControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private MedicoService medicoService;

    @MockitoBean
    private RecepcionistaService recepcionistaService;

    @MockitoBean
    private ConsultaService consultaService;

    @MockitoBean
    private HistoricoMedicoService historicoMedicoService;

    @MockitoBean
    private ExameMedicoService exameMedicoService;

    @Test
    void deveRetornar401QuandoNaoHaCredenciais() throws Exception {
        mockMvc.perform(get("/paciente/busca/all"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensagem").value("Autenticação necessária."));
    }

    @Test
    void deveRetornar403QuandoMedicoTentaCadastrarRecepcionista() throws Exception {
        mockMvc.perform(post("/recepcionista/cadastro")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRecepcionista()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(recepcionistaService);
    }

    @Test
    void devePermitirAdminCadastrarRecepcionista() throws Exception {
        UUID id = UUID.randomUUID();
        when(recepcionistaService.save(any())).thenReturn(new RecepcionistaResponse(id, "Ana", "98765432100",
                LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(post("/recepcionista/cadastro")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRecepcionista()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.status").value(true));

        ArgumentCaptor<RecepcionistaRequest> captor = ArgumentCaptor.forClass(RecepcionistaRequest.class);
        verify(recepcionistaService).save(captor.capture());
        assertThat(captor.getValue().usuario().email()).isEqualTo("ana@example.com");
        assertThat(captor.getValue().cpf()).isEqualTo("98765432100");
    }

    @Test
    void devePermitirRecepcionistaCadastrarPaciente() throws Exception {
        UUID id = UUID.randomUUID();
        when(pacienteService.save(any())).thenReturn(new PacienteResponse(id, "João", "12345678901",
                LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO));

        mockMvc.perform(post("/paciente/cadastro")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaciente()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.genero").value("MASCULINO"));

        ArgumentCaptor<PacienteRequest> captor = ArgumentCaptor.forClass(PacienteRequest.class);
        verify(pacienteService).save(captor.capture());
        assertThat(captor.getValue().usuario().email()).isEqualTo("joao@example.com");
        assertThat(captor.getValue().endereco()).isEqualTo("Rua Teste, 1");
    }

    @Test
    void deveRetornar403QuandoMedicoTentaCadastrarPaciente() throws Exception {
        mockMvc.perform(post("/paciente/cadastro")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaciente()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(pacienteService);
    }

    @Test
    void devePermitirAdminCadastrarMedico() throws Exception {
        UUID id = UUID.randomUUID();
        when(medicoService.save(any())).thenReturn(new MedicoResponse(id, "Dr. Carlos", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(post("/medico/cadastro")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedico()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Dr. Carlos"))
                .andExpect(jsonPath("$.crm").value("123456"))
                .andExpect(jsonPath("$.especialidade").value("CARDIOLOGIA"));

        ArgumentCaptor<MedicoRequest> captor = ArgumentCaptor.forClass(MedicoRequest.class);
        verify(medicoService).save(captor.capture());
        assertThat(captor.getValue().usuario().email()).isEqualTo("carlos@example.com");
        assertThat(captor.getValue().crm()).isEqualTo("123456");
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaCadastrarMedico() throws Exception {
        mockMvc.perform(post("/medico/cadastro")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedico()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(medicoService);
    }

    @Test
    void devePermitirQualquerUsuarioAutenticadoListarMedicos() throws Exception {
        when(medicoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/medico/busca/all")
                        .with(user("paciente").roles("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(medicoService).findAll();
    }

    @Test
    void devePermitirRecepcionistaAgendarConsulta() throws Exception {
        when(consultaService.agendarConsulta(any())).thenReturn(new ConsultaResponse(UUID.randomUUID(),
                "João", "Dr. Carlos", "Ana", LocalDateTime.now().plusDays(1), "AGENDADA", null, null));

        mockMvc.perform(post("/consulta/agendar")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConsulta()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGENDADA"))
                .andExpect(jsonPath("$.pacienteNome").value("João"));

        ArgumentCaptor<ConsultaRequest> captor = ArgumentCaptor.forClass(ConsultaRequest.class);
        verify(consultaService).agendarConsulta(captor.capture());
        assertThat(captor.getValue().observacoes()).isEqualTo("Primeira consulta");
        assertThat(captor.getValue().dataHora()).isAfter(LocalDateTime.now());
    }

    @Test
    void deveRetornar403QuandoMedicoTentaAgendarConsulta() throws Exception {
        mockMvc.perform(post("/consulta/agendar")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConsulta()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(consultaService);
    }

    @Test
    void devePermitirMedicoConsultarConsultasPorPaciente() throws Exception {
        when(consultaService.buscarConsultasPorPaciente(any())).thenReturn(List.of());

        mockMvc.perform(get("/consulta/paciente/{id}", UUID.randomUUID())
                        .with(user("medico").roles("MEDICO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(consultaService).buscarConsultasPorPaciente(any());
    }

    @Test
    void devePermitirMedicoCriarHistoricoMedico() throws Exception {
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID consultaId = UUID.randomUUID();
        when(historicoMedicoService.criarHistorico(any(), any(), any(), any()))
                .thenReturn(new HistoricoMedicoResponse(UUID.randomUUID(), "João", "Dr. Carlos",
                        "REALIZADA", "Hipertensão", "Losartana", null, LocalDateTime.now()));

        mockMvc.perform(post("/historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}",
                        pacienteId, medicoId, consultaId)
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"diagnostico":"Hipertensão","prescricao":"Losartana","observacoes":"Acompanhar"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diagnostico").value("Hipertensão"))
                .andExpect(jsonPath("$.consultaStatus").value("REALIZADA"));

        ArgumentCaptor<HistoricoMedicoRequest> captor = ArgumentCaptor.forClass(HistoricoMedicoRequest.class);
        verify(historicoMedicoService).criarHistorico(eq(pacienteId), eq(medicoId), eq(consultaId), captor.capture());
        assertThat(captor.getValue().diagnostico()).isEqualTo("Hipertensão");
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaCriarHistoricoMedico() throws Exception {
        mockMvc.perform(post("/historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}",
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"diagnostico":"Hipertensão"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(historicoMedicoService);
    }

    @Test
    void deveRetornar401QuandoNaoHaCredenciaisAoBuscarHistoricoMedico() throws Exception {
        mockMvc.perform(get("/historico-medico/paciente/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Autenticação necessária."));
    }

    @Test
    void devePermitirMedicoBuscarHistoricoMedicoPorPaciente() throws Exception {
        when(historicoMedicoService.buscarPorPaciente(any()))
                .thenReturn(List.of(new HistoricoMedicoDetalhadoResponse(UUID.randomUUID(), "João", "Dr. Carlos",
                        "REALIZADA", "Hipertensão", "Losartana", null, LocalDateTime.now(), List.of())));

        mockMvc.perform(get("/historico-medico/paciente/{id}", UUID.randomUUID())
                        .with(user("medico").roles("MEDICO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnostico").value("Hipertensão"))
                .andExpect(jsonPath("$[0].exames").isArray());

        verify(historicoMedicoService).buscarPorPaciente(any());
    }

    @Test
    void devePermitirAdminBuscarHistoricoMedicoPorPaciente() throws Exception {
        when(historicoMedicoService.buscarPorPaciente(any())).thenReturn(List.of());

        mockMvc.perform(get("/historico-medico/paciente/{id}", UUID.randomUUID())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deveRetornar403QuandoRecepcionistaBuscaHistoricoMedico() throws Exception {
        mockMvc.perform(get("/historico-medico/paciente/{id}", UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(historicoMedicoService);
    }

    @Test
    void devePermitirMedicoBuscarPacientePorCpf() throws Exception {
        when(pacienteService.findByCpf("12345678901"))
                .thenReturn(new PacienteResponse(UUID.randomUUID(), "João", "12345678901",
                        LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO));

        mockMvc.perform(get("/paciente/busca/{cpf}", "12345678901")
                        .with(user("medico").roles("MEDICO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João"));

        verify(pacienteService).findByCpf("12345678901");
    }

    @Test
    void devePermitirMedicoListarPacientes() throws Exception {
        when(pacienteService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/paciente/busca/all")
                        .with(user("medico").roles("MEDICO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(pacienteService).findAll();
    }

    @Test
    void deveRetornar403QuandoPacienteTentaListarPacientes() throws Exception {
        mockMvc.perform(get("/paciente/busca/all")
                        .with(user("paciente").roles("PACIENTE")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(pacienteService);
    }

    @Test
    void devePermitirMedicoAdicionarExame() throws Exception {
        UUID historicoId = UUID.randomUUID();
        LocalDateTime dataExame = LocalDateTime.of(2026, 1, 10, 10, 0);
        when(exameMedicoService.adicionarExame(any(), any()))
                .thenReturn(new ExameMedicoResponse(UUID.randomUUID(), "Hemograma", "Normal",
                        dataExame, null));

        mockMvc.perform(post("/exame-medico/adicionar/{id}", historicoId)
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoExame":"Hemograma","resultado":"Normal","dataExame":"2026-01-10T10:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoExame").value("Hemograma"))
                .andExpect(jsonPath("$.resultado").value("Normal"));

        ArgumentCaptor<ExameMedicoRequest> captor = ArgumentCaptor.forClass(ExameMedicoRequest.class);
        verify(exameMedicoService).adicionarExame(eq(historicoId), captor.capture());
        assertThat(captor.getValue().tipoExame()).isEqualTo("Hemograma");
        assertThat(captor.getValue().dataExame()).isEqualTo(dataExame);
        verifyNoInteractions(historicoMedicoService);
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaAdicionarExame() throws Exception {
        mockMvc.perform(post("/exame-medico/adicionar/{id}", UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoExame":"Hemograma","resultado":"Normal","dataExame":"2026-01-10T10:00:00"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(exameMedicoService);
    }

    @Test
    void devePermitirAdminAtualizarMedico() throws Exception {
        UUID id = UUID.randomUUID();
        when(medicoService.update(any(), any())).thenReturn(new MedicoResponse(id, "Dr. Carlos",
                "123456", Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(put("/medico/atualizar/{id}", id)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedico()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.crm").value("123456"));

        verify(medicoService).update(eq(id), any(MedicoRequest.class));
    }

    @Test
    void devePermitirUsuarioAutenticadoBuscarMedicosPorNome() throws Exception {
        when(medicoService.findByNome("Carlos")).thenReturn(List.of());

        mockMvc.perform(get("/medico/busca/{nome}", "Carlos")
                        .with(user("paciente").roles("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(medicoService).findByNome("Carlos");
    }

    @Test
    void devePermitirUsuarioAutenticadoBuscarMedicoPorCrm() throws Exception {
        when(medicoService.findByCrm("123456")).thenReturn(new MedicoResponse(UUID.randomUUID(), "Dr. Carlos",
                "123456", Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(get("/medico/busca/crm/{crm}", "123456")
                        .with(user("paciente").roles("PACIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crm").value("123456"))
                .andExpect(jsonPath("$.nome").value("Dr. Carlos"));

        verify(medicoService).findByCrm("123456");
    }

    @Test
    void devePermitirAdminExcluirMedico() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(medicoService).excluirMedico(any());

        mockMvc.perform(delete("/medico/delete/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(medicoService).excluirMedico(id);
    }

    @Test
    void devePermitirAdminAtualizarRecepcionista() throws Exception {
        UUID id = UUID.randomUUID();
        when(recepcionistaService.update(any(), any())).thenReturn(new RecepcionistaResponse(id,
                "Ana", "98765432100", LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(put("/recepcionista/atualizar/{id}", id)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRecepcionista()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Ana"));

        verify(recepcionistaService).update(eq(id), any(RecepcionistaRequest.class));
    }

    @Test
    void devePermitirAdminBuscarRecepcionistaPorCpf() throws Exception {
        when(recepcionistaService.findByCpf("98765432100")).thenReturn(new RecepcionistaResponse(UUID.randomUUID(),
                "Ana", "98765432100", LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(get("/recepcionista/busca/{cpf}", "98765432100")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.nome").value("Ana"));

        verify(recepcionistaService).findByCpf("98765432100");
    }

    @Test
    void devePermitirAdminListarRecepcionistas() throws Exception {
        when(recepcionistaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/recepcionista/busca/all")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(recepcionistaService).findAll();
    }

    @Test
    void devePermitirAdminExcluirRecepcionista() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(recepcionistaService).excluirRecepcionista(any());

        mockMvc.perform(delete("/recepcionista/delete/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(recepcionistaService).excluirRecepcionista(id);
    }

    @Test
    void devePermitirRecepcionistaAtualizarPaciente() throws Exception {
        UUID id = UUID.randomUUID();
        when(pacienteService.update(any(), any())).thenReturn(new PacienteResponse(id, "João",
                "12345678901", LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO));

        mockMvc.perform(put("/paciente/atualizar/{id}", id)
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaciente()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(pacienteService).update(eq(id), any(PacienteRequest.class));
    }

    @Test
    void devePermitirRecepcionistaAtualizarStatusDaConsulta() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultaService.atualizarStatus(any(), any())).thenReturn(new ConsultaResponse(id,
                "João", "Dr. Carlos", "Ana", LocalDateTime.now().plusDays(1), "CANCELADA", null, null));

        mockMvc.perform(put("/consulta/status/{id}", id)
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CANCELADA\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        verify(consultaService).atualizarStatus(id, Consulta.StatusConsulta.CANCELADA);
    }

    @Test
    void devePermitirMedicoAdicionarExameViaHistorico() throws Exception {
        UUID historicoId = UUID.randomUUID();
        when(historicoMedicoService.adicionarExame(any(), any()))
                .thenReturn(new ExameMedicoResponse(UUID.randomUUID(), "Hemograma", "Normal",
                        LocalDateTime.of(2026, 1, 10, 10, 0), null));

        mockMvc.perform(post("/historico-medico/adicionar-exame/{id}", historicoId)
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoExame":"Hemograma","resultado":"Normal","dataExame":"2026-01-10T10:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoExame").value("Hemograma"))
                .andExpect(jsonPath("$.resultado").value("Normal"));

        verify(historicoMedicoService).adicionarExame(eq(historicoId), any(ExameMedicoRequest.class));
        verifyNoInteractions(exameMedicoService);
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaExcluirPaciente() throws Exception {
        mockMvc.perform(delete("/paciente/delete/{id}", UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value("Acesso negado."));

        verifyNoInteractions(pacienteService);
    }

    @Test
    void devePermitirAdminExcluirPaciente() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(pacienteService).excluirPaciente(any());

        mockMvc.perform(delete("/paciente/delete/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(pacienteService).excluirPaciente(id);
    }

    @Test
    void deveRetornar400QuandoPacienteInvalido() throws Exception {
        mockMvc.perform(post("/paciente/cadastro")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","cpf":"123","usuario":{"email":"invalido","senha":"123"}}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Dados inválidos."))
                .andExpect(jsonPath("$.campos.cpf").value("CPF inválido. Deve conter 11 dígitos, sem pontuação."))
                .andExpect(jsonPath("$.campos.nome").value("Nome é obrigatório."))
                .andExpect(jsonPath("$.campos['usuario.email']").value("E-mail inválido."));

        verifyNoInteractions(pacienteService);
    }

    @Test
    void deveRetornar400QuandoDataDaConsultaEstaNoPassado() throws Exception {
        mockMvc.perform(post("/consulta/agendar")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":"%s","medicoId":"%s","recepcionistaId":"%s","dataHora":"2020-01-01T10:00:00"}
                                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.dataHora").value("A data da consulta não pode ser no passado."));

        verifyNoInteractions(consultaService);
    }

    private String jsonPaciente() {
        return """
                {
                  "nome": "João Silva",
                  "cpf": "12345678901",
                  "telefone": "(11) 91234-5678",
                  "endereco": "Rua Teste, 1",
                  "dataNascimento": "1990-01-01",
                  "genero": "MASCULINO",
                  "usuario": {"email": "joao@example.com", "senha": "123456"}
                }
                """;
    }

    private String jsonMedico() {
        return """
                {
                  "nome": "Dr. Carlos",
                  "telefone": "(11) 91234-5678",
                  "crm": "123456",
                  "especialidade": "CARDIOLOGIA",
                  "dataContratacao": "2023-01-01",
                  "status": true,
                  "usuario": {"email": "carlos@example.com", "senha": "123456"}
                }
                """;
    }

    private String jsonRecepcionista() {
        return """
                {
                  "cpf": "98765432100",
                  "nome": "Ana",
                  "telefone": "(11) 91234-5678",
                  "dataContratacao": "2023-01-01",
                  "status": true,
                  "usuario": {"email": "ana@example.com", "senha": "123456"}
                }
                """;
    }

    private String jsonConsulta() {
        return """
                {
                  "pacienteId": "%s",
                  "medicoId": "%s",
                  "recepcionistaId": "%s",
                  "dataHora": "%s",
                  "observacoes": "Primeira consulta"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1));
    }
}
