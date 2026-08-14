package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Endpoint para criar um paciente
    @PostMapping("/cadastro")
    public ResponseEntity<PacienteResponse> criarPaciente(@Valid @RequestBody PacienteRequest pacienteInput) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pacienteService.save(pacienteInput));  // Retorna 201 Created
    }

    // Endpoint para atualizar um paciente
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PacienteResponse> atualizarPaciente(@Valid @RequestBody PacienteRequest pacienteInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pacienteService.update(id, pacienteInput));  // Retorna 200 OK
    }

    // Endpoint para buscar um paciente pelo CPF
    @GetMapping("/busca/{cpf}")
    public ResponseEntity<PacienteResponse> buscarPaciente(@PathVariable String cpf) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pacienteService.findByCpf(cpf));  // Retorna 200 OK
    }

    // Endpoint para buscar todos os pacientes
    @GetMapping("/busca/all")
    public ResponseEntity<List<PacienteResponse>> buscarTodosPacientes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pacienteService.findAll());  // Retorna 200 OK
    }

    // Endpoint para excluir um paciente
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluirPaciente(@PathVariable UUID id) {
        pacienteService.excluirPaciente(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // Retorna 204 No Content
    }
}

