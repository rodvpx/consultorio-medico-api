package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.service.RecepcionistaService;
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

@Tag(name = "Recepcionistas")
@RestController
@RequestMapping("/recepcionista")
public class RecepcionistaController {

    private final RecepcionistaService recepcionistaService;

    @Autowired
    public RecepcionistaController(RecepcionistaService recepcionistaService) {
        this.recepcionistaService = recepcionistaService;
    }

    @Operation(summary = "Cadastra um recepcionista")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recepcionista cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado")
    })
    @PostMapping("/cadastro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaResponse> criarRecepcionista(
            @Valid @RequestBody RecepcionistaRequest recepcionistaInput) {

        RecepcionistaResponse output = recepcionistaService.save(recepcionistaInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @Operation(summary = "Atualiza um recepcionista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recepcionista atualizado"),
            @ApiResponse(responseCode = "404", description = "Recepcionista não encontrado")
    })
    @PutMapping("/atualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaResponse> atualizarRecepcionista(@Valid @RequestBody RecepcionistaRequest recepcionistaInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(recepcionistaService.update(id, recepcionistaInput));
    }

    @Operation(summary = "Busca recepcionista pelo CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recepcionista encontrado"),
            @ApiResponse(responseCode = "404", description = "Recepcionista não encontrado")
    })
    @GetMapping("/busca/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaResponse> buscarRecepcionista(@PathVariable String cpf) {
        return ResponseEntity.status(HttpStatus.OK).body(recepcionistaService.findByCpf(cpf));
    }

    @Operation(summary = "Lista todos os recepcionistas")
    @ApiResponse(responseCode = "200", description = "Lista de recepcionistas")
    @GetMapping("/busca/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecepcionistaResponse>> buscarTodosRecepcionistas() {
        return ResponseEntity.status(HttpStatus.OK).body(recepcionistaService.findAll());
    }

    @Operation(summary = "Exclui um recepcionista")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recepcionista excluído"),
            @ApiResponse(responseCode = "404", description = "Recepcionista não encontrado")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirRecepcionista(@PathVariable UUID id) {
        recepcionistaService.excluirRecepcionista(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
