package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.service.ExameMedicoService;
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

import java.util.UUID;

@Tag(name = "Exames médicos")
@RestController
@RequestMapping("/exame-medico")
public class ExameMedicoController {

    private final ExameMedicoService exameMedicoService;

    @Autowired
    public ExameMedicoController(ExameMedicoService exameMedicoService) {
        this.exameMedicoService = exameMedicoService;
    }

    @Operation(summary = "Adiciona um exame médico a um histórico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exame adicionado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Histórico médico não encontrado")
    })
    @PostMapping("/adicionar/{historicoMedicoId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<ExameMedicoResponse> adicionarExame(@PathVariable UUID historicoMedicoId,
                                                            @Valid @RequestBody ExameMedicoRequest exameInput) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exameMedicoService.adicionarExame(historicoMedicoId, exameInput));
    }
}
