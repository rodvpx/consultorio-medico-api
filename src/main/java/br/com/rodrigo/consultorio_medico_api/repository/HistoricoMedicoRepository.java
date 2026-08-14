package br.com.rodrigo.consultorio_medico_api.repository;
import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoricoMedicoRepository extends JpaRepository<HistoricoMedico, UUID> {
}
