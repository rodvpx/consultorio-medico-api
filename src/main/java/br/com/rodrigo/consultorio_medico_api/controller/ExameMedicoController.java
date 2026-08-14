package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.service.ExameMedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/exame-medico")
public class ExameMedicoController {

    private final ExameMedicoService exameMedicoService;

    @Autowired
    public ExameMedicoController(ExameMedicoService exameMedicoService) {
        this.exameMedicoService = exameMedicoService;
    }

    // Endpoint para adicionar um exame médico
    @PostMapping("/adicionar/{historicoMedicoId}")
    public ResponseEntity<ExameMedicoResponse> adicionarExame(@PathVariable UUID historicoMedicoId,
                                                            @Valid @RequestBody ExameMedicoRequest exameInput) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exameMedicoService.adicionarExame(historicoMedicoId, exameInput));  // Retorna 201 Created
    }
}

