package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.consulta.ConsultaResponse;
import br.com.rodrigo.consultorio_medico_api.entity.Consulta;
import br.com.rodrigo.consultorio_medico_api.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Consultas")
@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final ConsultaService consultaService;

    @Autowired
    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @Operation(summary = "Agenda uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta agendada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente, médico ou recepcionista não encontrado")
    })
    @PostMapping("/agendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ConsultaResponse> agendarConsulta(@Valid @RequestBody ConsultaRequest consultaInput) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultaService.agendarConsulta(consultaInput));
    }

    @Operation(summary = "Atualiza o status de uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ConsultaResponse> atualizarStatus(@PathVariable UUID id, @RequestBody Consulta.StatusConsulta status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(consultaService.atualizarStatus(id, status));
    }

    @Operation(summary = "Busca consultas de um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas encontradas"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'MEDICO')")
    public ResponseEntity<List<ConsultaResponse>> buscarConsultasPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(consultaService.buscarConsultasPorPaciente(pacienteId));
    }
}
