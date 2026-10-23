package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.exception.ConflitoException;
import br.com.rodrigo.consultorio_medico_api.exception.RecursoNaoEncontradoException;
import br.com.rodrigo.consultorio_medico_api.mapper.MedicoMapper;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedicoMapper medicoMapper;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, MedicoMapper medicoMapper) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.medicoMapper = medicoMapper;
    }

    // Criar médico
    @Transactional
    public MedicoResponse save(MedicoRequest input) {
        if (verificarSeCrmExiste(input.crm())) {
            throw new ConflitoException("Já existe um médico cadastrado com o CRM informado.");
        }
        if (verificarSeEmailJaCadastrado(input.usuario().email())) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail informado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(input.usuario().email());
        usuario.setSenha(passwordEncoder.encode(input.usuario().senha()));
        usuario.setRole(Role.MEDICO);

        usuarioRepository.save(usuario);

        Medico medico = medicoMapper.toEntity(input);
        medico.setUsuario(usuario);

        return medicoMapper.toOutput(medicoRepository.save(medico));
    }

    // Atualizar médico
    @Transactional
    public MedicoResponse update(UUID id, MedicoRequest input) {
        Medico medicoExistente = medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado."));

        if (!medicoExistente.getCrm().equals(input.crm()) && verificarSeCrmExiste(input.crm())) {
            throw new ConflitoException("Já existe um médico cadastrado com o CRM informado.");
        }

        medicoExistente.setNome(input.nome());
        medicoExistente.setTelefone(input.telefone());
        medicoExistente.setCrm(input.crm());
        medicoExistente.setEspecialidade(input.especialidade());
        medicoExistente.setDataContratacao(input.dataContratacao());
        medicoExistente.setStatus(input.status());

        return medicoMapper.toOutput(medicoRepository.save(medicoExistente));
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> findByNome(String nome) {
        List<Medico> medicos = medicoRepository.findByNome(nome);

        if (medicos.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum médico encontrado com o nome '" + nome + "'.");
        }

        return medicoMapper.toOutputList(medicos);
    }

    @Transactional(readOnly = true)
    public MedicoResponse findByCrm(String crm) {
        Medico medico = medicoRepository.findByCrm(crm)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico com CRM '" + crm + "' não encontrado."));

        return medicoMapper.toOutput(medico);
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> findAll() {
        return medicoMapper.toOutputList(medicoRepository.findAll());
    }

    @Transactional
    public void excluirMedico(UUID id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado."));

        usuarioRepository.delete(medico.getUsuario());
        medicoRepository.delete(medico);
    }

    public boolean verificarSeCrmExiste(String crm) {
        return medicoRepository.existsByCrm(crm);
    }

    public boolean verificarSeEmailJaCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
