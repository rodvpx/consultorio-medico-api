package br.com.rodrigo.consultorio_medico_api.controller;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/medicos")
public class MedicoController {

    private final MedicoRepository medicoRepository;
    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoRepository medicoRepository, MedicoService medicoService) {
        this.medicoRepository = medicoRepository;
        this.medicoService = medicoService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<MedicoResponse> criarMedico(@Valid @RequestBody MedicoRequest medicoInput) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.save(medicoInput));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<MedicoResponse> atualizarMedico(@Valid @RequestBody MedicoRequest medicoInput, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.update(id, medicoInput));
    }

    @GetMapping("/busca/{nome}")
    public ResponseEntity<List<MedicoResponse>> buscarMedicosPorNome(@PathVariable String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findByNome(nome));
    }

    @GetMapping("/busca/crm/{crm}")
    public ResponseEntity<MedicoResponse> buscarMedicoPorCrm(@PathVariable String crm) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findByCrm(crm));
    }

    @GetMapping("/busca/all")
    public ResponseEntity<List<MedicoResponse>> buscarTodosMedicos() {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.findAll());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluirMedico(@PathVariable UUID id) {
        medicoService.excluirMedico(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
