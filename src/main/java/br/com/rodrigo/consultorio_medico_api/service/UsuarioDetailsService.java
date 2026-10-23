package br.com.rodrigo.consultorio_medico_api.service;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail informado."));

        // Usuário legado sem papel cadastrado recebe o papel de menor privilégio.
        String authority = usuario.getRole() != null
                ? usuario.getRole().getAuthority()
                : Role.PACIENTE.getAuthority();

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(authority)
                .build();
    }
}
