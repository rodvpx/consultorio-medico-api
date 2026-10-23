package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaRequest;
import br.com.rodrigo.consultorio_medico_api.dto.recepcionista.RecepcionistaResponse;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.RecepcionistaMapper;
import br.com.rodrigo.consultorio_medico_api.entity.Recepcionista;
import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.RecepcionistaRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public RecepcionistaResponse save(RecepcionistaRequest input) {
        if (verificarSeCpfExiste(input.cpf())) {
            throw new ConflitoException("Já existe um recepcionista cadastrado com o CPF informado.");
        }
        if (verificarSeEmailJaCadastrado(input.usuario().email())) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail informado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(input.usuario().email());
        usuario.setSenha(passwordEncoder.encode(input.usuario().senha()));
        usuario.setRole(Role.RECEPCIONISTA);

        usuarioRepository.save(usuario);

        Recepcionista recepcionista = recepcionistaMapper.toEntity(input);
        recepcionista.setUsuario(usuario);

        return recepcionistaMapper.toOutput(recepcionistaRepository.save(recepcionista));
    }

    // Atualizar recepcionista
    @Transactional
    public RecepcionistaResponse update(UUID id, RecepcionistaRequest input) {
        Recepcionista existente = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recepcionista não encontrado."));

        if (!existente.getCpf().equals(input.cpf()) && verificarSeCpfExiste(input.cpf())) {
            throw new ConflitoException("Já existe um recepcionista cadastrado com o CPF informado.");
        }

        existente.setNome(input.nome());
        existente.setCpf(input.cpf());
        existente.setTelefone(input.telefone());
        existente.setDataContratacao(input.dataContratacao());
        existente.setStatus(input.status());

        return recepcionistaMapper.toOutput(recepcionistaRepository.save(existente));
    }

    @Transactional(readOnly = true)
    public RecepcionistaResponse findByCpf(String cpf) {
        Recepcionista recepcionista = recepcionistaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recepcionista com CPF '" + cpf + "' não encontrado."));

        return recepcionistaMapper.toOutput(recepcionista);
    }

    @Transactional(readOnly = true)
    public List<RecepcionistaResponse> findAll() {
        return recepcionistaMapper.toOutputList(recepcionistaRepository.findAll());
    }

    @Transactional
    public void excluirRecepcionista(UUID id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recepcionista não encontrado."));

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
