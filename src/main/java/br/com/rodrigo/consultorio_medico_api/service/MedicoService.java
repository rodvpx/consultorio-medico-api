package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoRequest;
import br.com.rodrigo.consultorio_medico_api.dto.medico.MedicoResponse;
import br.com.rodrigo.consultorio_medico_api.mapper.MedicoMapper;
import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.MedicoRepository;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public MedicoResponse save(MedicoRequest input) {
        // Criptografar senha
        String senhaCriptografada = passwordEncoder.encode(input.usuario().senha());

        // Verificar CRM e e-mail
        if (verificarSeCrmExiste(input.crm()) || verificarSeEmailJaCadastrado(input.usuario().email())) {
            System.out.println("ERROR: Crm ou email já cadastrados");
            return null;
        }

        // Criar e salvar usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(input.usuario().email());
        usuario.setSenha(senhaCriptografada);
        usuario.setRole("ROLE_MEDICO");

        usuarioRepository.save(usuario);

        // Criar entidade Médico com o usuário associado
        Medico medico = medicoMapper.toEntity(input);
        medico.setUsuario(usuario);

        // Salvar e retornar o DTO de saída
        return medicoMapper.toOutput(medicoRepository.save(medico));
    }

    // Atualizar médico
    public MedicoResponse update(UUID id, MedicoRequest input) {
        Medico medicoExistente = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        medicoExistente.setNome(input.nome());
        medicoExistente.setTelefone(input.telefone());
        medicoExistente.setCrm(input.crm());
        medicoExistente.setEspecialidade(input.especialidade());
        medicoExistente.setDataContratacao(input.dataContratacao());
        medicoExistente.setStatus(input.status());

        return medicoMapper.toOutput(medicoRepository.save(medicoExistente));
    }

    public List<MedicoResponse> findByNome(String nome) {
        List<Medico> medicos = medicoRepository.findByNome(nome);

        if (medicos.isEmpty()) {
            throw new RuntimeException("Nenhum médico encontrado com o nome '" + nome + "'");
        }

        return medicoMapper.toOutputList(medicos);
    }

    public MedicoResponse findByCrm(String crm) {
        Medico medico = medicoRepository.findByCrm(crm)
                .orElseThrow(() -> new RuntimeException("Médico com CRM '" + crm + "' não encontrado"));

        return medicoMapper.toOutput(medico);
    }

    public List<MedicoResponse> findAll() {
        return medicoMapper.toOutputList(medicoRepository.findAll());
    }

    public void excluirMedico(UUID id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

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
