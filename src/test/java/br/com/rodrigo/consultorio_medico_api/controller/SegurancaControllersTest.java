package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.config.SecurityConfig;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.service.ConsultaService;
import br.com.rodrigo.consultorio_medico_api.service.ExameMedicoService;
import br.com.rodrigo.consultorio_medico_api.service.HistoricoMedicoService;
import br.com.rodrigo.consultorio_medico_api.service.MedicoService;
import br.com.rodrigo.consultorio_medico_api.service.PacienteService;
import br.com.rodrigo.consultorio_medico_api.service.RecepcionistaService;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoMedicoTentaCadastrarRecepcionista() throws Exception {
        mockMvc.perform(post("/recepcionista/cadastro")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRecepcionista()))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminCadastrarRecepcionista() throws Exception {
        when(recepcionistaService.save(any())).thenReturn(new RecepcionistaResponse(UUID.randomUUID(), "Ana", "98765432100", LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(post("/recepcionista/cadastro")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRecepcionista()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void devePermitirRecepcionistaCadastrarPaciente() throws Exception {
        when(pacienteService.save(any())).thenReturn(new PacienteResponse(UUID.randomUUID(), "João", "12345678901", LocalDate.of(1990, 1, 1), Paciente.Genero.MASCULINO));

        mockMvc.perform(post("/paciente/cadastro")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaciente()))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403QuandoMedicoTentaCadastrarPaciente() throws Exception {
        mockMvc.perform(post("/paciente/cadastro")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaciente()))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminCadastrarMedico() throws Exception {
        when(medicoService.save(any())).thenReturn(new MedicoResponse(UUID.randomUUID(), "Dr. Carlos", "123456",
                Medico.Especialidade.CARDIOLOGIA, LocalDate.of(2023, 1, 1), true));

        mockMvc.perform(post("/medico/cadastro")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedico()))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaCadastrarMedico() throws Exception {
        mockMvc.perform(post("/medico/cadastro")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMedico()))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirQualquerUsuarioAutenticadoListarMedicos() throws Exception {
        when(medicoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/medico/busca/all")
                        .with(user("paciente").roles("PACIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirRecepcionistaAgendarConsulta() throws Exception {
        when(consultaService.agendarConsulta(any())).thenReturn(new ConsultaResponse(UUID.randomUUID(),
                "João", "Dr. Carlos", "Ana", LocalDateTime.now().plusDays(1), "AGENDADA", null, null));

        mockMvc.perform(post("/consulta/agendar")
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConsulta()))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403QuandoMedicoTentaAgendarConsulta() throws Exception {
        mockMvc.perform(post("/consulta/agendar")
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConsulta()))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirMedicoConsultarConsultasPorPaciente() throws Exception {
        when(consultaService.buscarConsultasPorPaciente(any())).thenReturn(List.of());

        mockMvc.perform(get("/consulta/paciente/{id}", UUID.randomUUID())
                        .with(user("medico").roles("MEDICO")))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirMedicoCriarHistoricoMedico() throws Exception {
        when(historicoMedicoService.criarHistorico(any(), any(), any(), any()))
                .thenReturn(new HistoricoMedicoResponse(UUID.randomUUID(), "João", "Dr. Carlos",
                        "REALIZADA", "Hipertensão", "Losartana", null, LocalDateTime.now()));

        mockMvc.perform(post("/historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}",
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"diagnostico":"Hipertensão","prescricao":"Losartana","observacoes":"Acompanhar"}
                                """))
                .andExpect(status().isCreated());
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
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirMedicoAdicionarExame() throws Exception {
        when(exameMedicoService.adicionarExame(any(), any()))
                .thenReturn(new ExameMedicoResponse(UUID.randomUUID(), "Hemograma", "Normal",
                        LocalDateTime.now(), null));

        mockMvc.perform(post("/exame-medico/adicionar/{id}", UUID.randomUUID())
                        .with(user("medico").roles("MEDICO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoExame":"Hemograma","resultado":"Normal","dataExame":"2026-01-10T10:00:00"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaAdicionarExame() throws Exception {
        mockMvc.perform(post("/exame-medico/adicionar/{id}", UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoExame":"Hemograma","resultado":"Normal","dataExame":"2026-01-10T10:00:00"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoRecepcionistaTentaExcluirPaciente() throws Exception {
        mockMvc.perform(delete("/paciente/delete/{id}", UUID.randomUUID())
                        .with(user("recepcao").roles("RECEPCIONISTA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAdminExcluirPaciente() throws Exception {
        doNothing().when(pacienteService).excluirPaciente(any());

        mockMvc.perform(delete("/paciente/delete/{id}", UUID.randomUUID())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
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
                .andExpect(jsonPath("$.campos").exists());
    }

    @Test
    void deveRetornar400QuandoDataDaConsultaEstaNoPassado() throws Exception {
        mockMvc.perform(post("/consulta/agendar")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":"%s","medicoId":"%s","recepcionistaId":"%s","dataHora":"2020-01-01T10:00:00"}
                                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isBadRequest());
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
