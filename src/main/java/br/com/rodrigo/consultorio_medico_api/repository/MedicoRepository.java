package br.com.rodrigo.consultorio_medico_api.repository;

import br.com.rodrigo.consultorio_medico_api.entity.Medico;
import br.com.rodrigo.consultorio_medico_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicoRepository extends JpaRepository<Medico, UUID> {

    // Buscar médico por nome
    List<Medico> findByNome(String nome);

    // Buscar médico por CRM
    Optional<Medico> findByCrm(String crm);

    // Verificar se o CRM já está em uso
    boolean existsByCrm(String crm);

    // Buscar todos os médicos (já disponível via JpaRepository)
    List<Medico> findAll();

    Optional<Medico> findById(UUID id);

}
