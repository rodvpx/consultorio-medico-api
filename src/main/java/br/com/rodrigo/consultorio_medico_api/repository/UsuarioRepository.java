package br.com.rodrigo.consultorio_medico_api.repository;

import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Usuario findByEmail(String email);

    boolean existsByEmail(String email);
}
