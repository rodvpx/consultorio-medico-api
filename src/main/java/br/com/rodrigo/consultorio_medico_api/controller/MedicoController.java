package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.service.MedicoService;
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

@Tag(name = "Médicos")
@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @Operation(summary = "Cadastra um médico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Médico cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CRM ou e-mail já cadastrado")
    })
    @PostMapping("/cadastro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoResponse> criarMedico(@Valid @RequestBody MedicoRequest medicoInput) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.save(medicoInput));
    }

    @Operation(summary = "Atualiza um médico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Médico atualizado"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado"),
            @ApiResponse(responseCode = "409", description = "CRM já cadastrado")
    })
    @PutMapping("/atualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoResponse> atualizarMedico(@Valid @RequestBody MedicoRequest medicoInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.update(id, medicoInput));
    }

    @Operation(summary = "Busca médicos pelo nome")
    @ApiResponse(responseCode = "200", description = "Médicos encontrados")
    @GetMapping("/busca/{nome}")
    public ResponseEntity<List<MedicoResponse>> buscarMedicosPorNome(@PathVariable String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findByNome(nome));
    }

    @Operation(summary = "Busca médico pelo CRM")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Médico encontrado"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado")
    })
    @GetMapping("/busca/crm/{crm}")
    public ResponseEntity<MedicoResponse> buscarMedicoPorCrm(@PathVariable String crm) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findByCrm(crm));
    }

    @Operation(summary = "Lista todos os médicos")
    @ApiResponse(responseCode = "200", description = "Lista de médicos")
    @GetMapping("/busca/all")
    public ResponseEntity<List<MedicoResponse>> buscarTodosMedicos() {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findAll());
    }

    @Operation(summary = "Exclui um médico")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Médico excluído"),
            @ApiResponse(responseCode = "404", description = "Médico não encontrado")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirMedico(@PathVariable UUID id) {
        medicoService.excluirMedico(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
