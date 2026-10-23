package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.exame_medico.ExameMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoDetalhadoResponse;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.historico_medico.HistoricoMedicoResponse;
import br.com.rodrigo.consultorio_medico_api.service.ExameMedicoService;
import br.com.rodrigo.consultorio_medico_api.service.HistoricoMedicoService;
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

@Tag(name = "Histórico médico")
@RestController
@RequestMapping("/historico-medico")
public class HistoricoMedicoController {

    private final HistoricoMedicoService historicoMedicoService;
    private final ExameMedicoService exameMedicoService;

    @Autowired
    public HistoricoMedicoController(HistoricoMedicoService historicoMedicoService,
                                     ExameMedicoService exameMedicoService) {
        this.historicoMedicoService = historicoMedicoService;
        this.exameMedicoService = exameMedicoService;
    }

    @Operation(summary = "Cria um histórico médico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Histórico criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente, médico ou consulta não encontrado")
    })
    @PostMapping("/criar/{pacienteId}/{medicoId}/{consultaId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<HistoricoMedicoResponse> criarHistorico(@PathVariable UUID pacienteId,
                                                                @PathVariable UUID medicoId,
                                                                @PathVariable UUID consultaId,
                                                                @Valid @RequestBody HistoricoMedicoRequest historicoInput) {
        HistoricoMedicoResponse historicoMedicoOutput = historicoMedicoService.criarHistorico(pacienteId, medicoId, consultaId, historicoInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(historicoMedicoOutput);
    }

    @Operation(summary = "Busca os históricos médicos de um paciente, com os exames")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Históricos encontrados"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<List<HistoricoMedicoDetalhadoResponse>> buscarHistoricosPorPaciente(
            @PathVariable UUID pacienteId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(historicoMedicoService.buscarPorPaciente(pacienteId));
    }

    /**
     * Mantido por compatibilidade. O endpoint canônico é
     * {@code POST /exame-medico/adicionar/{historicoMedicoId}} e ambos usam o
     * mesmo método de {@link ExameMedicoService}.
     */
    @Deprecated
    @Operation(
            summary = "Adiciona um exame a um histórico médico (obsoleto)",
            description = "Endpoint mantido por compatibilidade. Use POST /exame-medico/adicionar/{historicoMedicoId}.",
            deprecated = true
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exame adicionado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Histórico médico não encontrado")
    })
    @PostMapping("/adicionar-exame/{historicoMedicoId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    public ResponseEntity<ExameMedicoResponse> adicionarExame(@PathVariable UUID historicoMedicoId,
                                                            @Valid @RequestBody ExameMedicoRequest exameInput) {
        ExameMedicoResponse exameMedicoOutput = exameMedicoService.adicionarExame(historicoMedicoId, exameInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameMedicoOutput);
    }
}
