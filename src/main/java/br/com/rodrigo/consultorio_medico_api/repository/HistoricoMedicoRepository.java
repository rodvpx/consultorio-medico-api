package br.com.rodrigo.consultorio_medico_api.repository;

import br.com.rodrigo.consultorio_medico_api.entity.HistoricoMedico;
import br.com.rodrigo.consultorio_medico_api.entity.Paciente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoMedicoRepository extends JpaRepository<HistoricoMedico, UUID> {

    @EntityGraph(attributePaths = "exames")
    List<HistoricoMedico> findByPaciente(Paciente paciente);
}
