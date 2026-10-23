package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.service.PacienteService;
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

@Tag(name = "Pacientes")
@RestController
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @Operation(summary = "Cadastra um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado")
    })
    @PostMapping("/cadastro")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<PacienteResponse> criarPaciente(@Valid @RequestBody PacienteRequest pacienteInput) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.save(pacienteInput));
    }

    @Operation(summary = "Atualiza um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente atualizado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    })
    @PutMapping("/atualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<PacienteResponse> atualizarPaciente(@Valid @RequestBody PacienteRequest pacienteInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(pacienteService.update(id, pacienteInput));
    }

    @Operation(summary = "Busca paciente pelo CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @GetMapping("/busca/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<PacienteResponse> buscarPaciente(@PathVariable String cpf) {
        return ResponseEntity.status(HttpStatus.OK).body(pacienteService.findByCpf(cpf));
    }

    @Operation(summary = "Lista todos os pacientes")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes")
    @GetMapping("/busca/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<PacienteResponse>> buscarTodosPacientes() {
        return ResponseEntity.status(HttpStatus.OK).body(pacienteService.findAll());
    }

    @Operation(summary = "Exclui um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paciente excluído"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirPaciente(@PathVariable UUID id) {
        pacienteService.excluirPaciente(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
