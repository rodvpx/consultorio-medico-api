package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.service.HistoricoMedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/historico-medico")
public class HistoricoMedicoController {

    private final HistoricoMedicoService historicoMedicoService;

    @Autowired
    public HistoricoMedicoController(HistoricoMedicoService historicoMedicoService) {
        this.historicoMedicoService = historicoMedicoService;
    }

    // Endpoint para criar um histórico médico
    @PostMapping("/criar/{pacienteId}/{medicoId}/{consultaId}")
    public ResponseEntity<HistoricoMedicoResponse> criarHistorico(@PathVariable UUID pacienteId,
                                                                @PathVariable UUID medicoId,
                                                                @PathVariable UUID consultaId,
                                                                @Valid @RequestBody HistoricoMedicoRequest historicoInput) {
        HistoricoMedicoResponse historicoMedicoOutput = historicoMedicoService.criarHistorico(pacienteId, medicoId, consultaId, historicoInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(historicoMedicoOutput);  // Retorna 201 Created
    }

    // Endpoint para adicionar um exame médico ao histórico
    @PostMapping("/adicionar-exame/{historicoMedicoId}")
    public ResponseEntity<ExameMedicoResponse> adicionarExame(@PathVariable UUID historicoMedicoId,
                                                            @Valid @RequestBody ExameMedicoRequest exameInput) {
        ExameMedicoResponse exameMedicoOutput = historicoMedicoService.adicionarExame(historicoMedicoId, exameInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameMedicoOutput);  // Retorna 201 Created
    }
}

