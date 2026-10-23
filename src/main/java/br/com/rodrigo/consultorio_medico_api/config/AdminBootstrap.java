package br.com.rodrigo.consultorio_medico_api.config;

import br.com.rodrigo.consultorio_medico_api.entity.Role;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import br.com.rodrigo.consultorio_medico_api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro usuário ADMIN a partir das variáveis de ambiente.
 * A operação é idempotente: se o e-mail já existir, nada é alterado.
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final boolean habilitado;

    public AdminBootstrap(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${ADMIN_EMAIL:}") String adminEmail,
                          @Value("${ADMIN_PASSWORD:}") String adminPassword,
                          @Value("${app.admin.bootstrap-enabled:true}") boolean habilitado) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.habilitado = habilitado;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.warn("ADMIN_EMAIL e ADMIN_PASSWORD não configurados. Nenhum ADMIN inicial será criado.");
            return;
        }

        if (usuarioRepository.existsByEmail(adminEmail)) {
            log.info("Usuário ADMIN inicial já existe para o e-mail configurado.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setEmail(adminEmail.trim());
        admin.setSenha(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);
        log.info("Usuário ADMIN inicial criado a partir das variáveis de ambiente.");
    }
}
