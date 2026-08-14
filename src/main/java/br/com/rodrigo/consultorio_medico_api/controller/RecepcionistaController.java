package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.service.RecepcionistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recepcionista")
public class RecepcionistaController {

    private final RecepcionistaService recepcionistaService;

    @Autowired
    public RecepcionistaController(RecepcionistaService recepcionistaService) {
        this.recepcionistaService = recepcionistaService;
    }

    // Endpoint para criar um recepcionista (exige ROLE_ADMIN)
    @PostMapping("/cadastro")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecepcionistaResponse> criarRecepcionista(
            @Valid @RequestBody RecepcionistaRequest recepcionistaInput) {

        RecepcionistaResponse output = recepcionistaService.save(recepcionistaInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    // Endpoint para atualizar um recepcionista
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<RecepcionistaResponse> atualizarRecepcionista(@Valid @RequestBody RecepcionistaRequest recepcionistaInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recepcionistaService.update(id, recepcionistaInput));  // Retorna 200 OK
    }

    // Endpoint para buscar um recepcionista pelo CPF
    @GetMapping("/busca/{cpf}")
    public ResponseEntity<RecepcionistaResponse> buscarRecepcionista(@PathVariable String cpf) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recepcionistaService.findByCpf(cpf));  // Retorna 200 OK
    }

    // Endpoint para buscar todos os recepcionistas
    @GetMapping("/busca/all")
    public ResponseEntity<List<RecepcionistaResponse>> buscarTodosRecepcionistas() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recepcionistaService.findAll());  // Retorna 200 OK
    }

    // Endpoint para excluir um recepcionista
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluirRecepcionista(@PathVariable UUID id) {
        recepcionistaService.excluirRecepcionista(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // Retorna 204 No Content
    }
}

