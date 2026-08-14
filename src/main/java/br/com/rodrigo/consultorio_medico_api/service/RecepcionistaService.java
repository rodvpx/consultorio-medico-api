package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.mapper.RecepcionistaMapper;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository recepcionistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecepcionistaMapper recepcionistaMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RecepcionistaService(RecepcionistaRepository recepcionistaRepository, UsuarioRepository usuarioRepository, RecepcionistaMapper recepcionistaMapper, PasswordEncoder passwordEncoder) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.recepcionistaMapper = recepcionistaMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar recepcionista
    public RecepcionistaResponse save(RecepcionistaRequest input) {
        // Verifica CPF e e-mail
        if (verificarSeCpfExiste(input.cpf()) || verificarSeEmailJaCadastrado(input.usuario().email())) {
            System.out.println("ERROR: CPF ou email já cadastrados");
            return null;
        }

        // Cria e salva o usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(input.usuario().email());
        usuario.setSenha(passwordEncoder.encode(input.usuario().senha()));
        usuario.setRole("ROLE_RECEPCIONISTA");

        usuarioRepository.save(usuario);

        // Cria e salva o recepcionista
        Recepcionista recepcionista = recepcionistaMapper.toEntity(input);
        recepcionista.setUsuario(usuario);

        return recepcionistaMapper.toOutput(recepcionistaRepository.save(recepcionista));
    }

    // Atualizar recepcionista
    public RecepcionistaResponse update(UUID id, RecepcionistaRequest input) {
        Recepcionista existente = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado"));

        existente.setNome(input.nome());
        existente.setCpf(input.cpf());
        existente.setTelefone(input.telefone());
        existente.setDataContratacao(input.dataContratacao());
        existente.setStatus(input.status());

        return recepcionistaMapper.toOutput(recepcionistaRepository.save(existente));
    }

    public RecepcionistaResponse findByCpf(String cpf) {
        Recepcionista recepcionista = recepcionistaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Recepcionista com CPF '" + cpf + "' não encontrado"));

        return recepcionistaMapper.toOutput(recepcionista);
    }

    public List<RecepcionistaResponse> findAll() {
        return recepcionistaMapper.toOutputList(recepcionistaRepository.findAll());
    }

    public void excluirRecepcionista(UUID id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado"));

        usuarioRepository.delete(recepcionista.getUsuario());
        recepcionistaRepository.delete(recepcionista);
    }

    public boolean verificarSeCpfExiste(String cpf) {
        return recepcionistaRepository.existsByCpf(cpf);
    }

    public boolean verificarSeEmailJaCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
