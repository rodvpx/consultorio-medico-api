package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteRequest;
import br.com.rodrigo.consultorio_medico_api.dto.paciente.PacienteResponse;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.PacienteMapper;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.PacienteRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacienteMapper pacienteMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository, PacienteMapper pacienteMapper, PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacienteMapper = pacienteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Criar paciente
    public PacienteResponse save(PacienteRequest input) {
        if (verificarSeCpfExiste(input.cpf())) {
            throw new ConflitoException("Já existe um paciente cadastrado com o CPF informado.");
        }
        if (verificarSeEmailJaCadastrado(input.usuario().email())) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail informado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(input.usuario().email());
        usuario.setSenha(passwordEncoder.encode(input.usuario().senha()));
        usuario.setRole(Role.PACIENTE);

        usuarioRepository.save(usuario);

        Paciente paciente = pacienteMapper.toEntity(input);
        paciente.setUsuario(usuario);

        return pacienteMapper.toOutput(pacienteRepository.save(paciente));
    }

    // Atualizar paciente
    public PacienteResponse update(UUID id, PacienteRequest input) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        if (!pacienteExistente.getCpf().equals(input.cpf()) && verificarSeCpfExiste(input.cpf())) {
            throw new ConflitoException("Já existe um paciente cadastrado com o CPF informado.");
        }

        pacienteExistente.setNome(input.nome());
        pacienteExistente.setCpf(input.cpf());
        pacienteExistente.setTelefone(input.telefone());
        pacienteExistente.setEndereco(input.endereco());
        pacienteExistente.setDataNascimento(input.dataNascimento());
        pacienteExistente.setGenero(input.genero());

        return pacienteMapper.toOutput(pacienteRepository.save(pacienteExistente));
    }

    // Buscar paciente por CPF
    public PacienteResponse findByCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente com CPF '" + cpf + "' não encontrado."));

        return pacienteMapper.toOutput(paciente);
    }

    // Buscar todos os pacientes
    public List<PacienteResponse> findAll() {
        return pacienteMapper.toOutputList(pacienteRepository.findAll());
    }

    // Excluir paciente
    public void excluirPaciente(UUID id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        usuarioRepository.delete(paciente.getUsuario());
        pacienteRepository.delete(paciente);
    }

    // Verificar se o CPF já está em uso
    public boolean verificarSeCpfExiste(String cpf) {
        return pacienteRepository.existsByCpf(cpf);
    }

    // Verificar se o e-mail já está em uso (para o paciente)
    public boolean verificarSeEmailJaCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
